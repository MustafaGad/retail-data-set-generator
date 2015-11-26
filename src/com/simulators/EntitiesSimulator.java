package com.simulators;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.HDFSReporter;
import com.entities.EntityContainer;
import com.entities.User;

public class EntitiesSimulator {

	private Properties properties;
	private List<UserActionSimulator> userSimulators;
	private Iterator<UserActionSimulator> userSimulatorsItr;
	private long timeStamp;
	private long simulationDurationInHours;
	private long simulationDurationInHoursRemaining;
	private Semaphore endOfSimulationCondition;
	private boolean endOfSimulation = false;
	protected long monitoringCheckInterval = 30 * 1000;
	private int transactionID;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  HH");

	public EntitiesSimulator(Properties properties, long simulationDuration, TimeUnit timeUnit) {
		this(properties, System.currentTimeMillis(), simulationDuration, timeUnit);
	}

	public EntitiesSimulator(Properties properties, long startTimeStamp, long simulationDuration,
			TimeUnit timeUnit) {
		simulationDurationInHours = timeUnit.toHours(simulationDuration);
		simulationDurationInHoursRemaining = simulationDurationInHours;
		this.properties = properties;
		userSimulators = new ArrayList<UserActionSimulator>(EntityContainer.getUsers().size());
		for (User user : EntityContainer.getUsers()) {
			userSimulators.add(new UserActionSimulator(user));
		}
		userSimulatorsItr = userSimulators.iterator();
		this.timeStamp = startTimeStamp;
		endOfSimulationCondition = new Semaphore(1);
	}

	public long getCurrentTime() {
		return timeStamp;
	}

	public void incrementTime(long deltaTime) {
		timeStamp += deltaTime;
	}

	public UserActionSimulator getUserSimulator() {
		synchronized (userSimulators) {
			if (!userSimulatorsItr.hasNext())
				emptyQueueProcedure();
			if (endOfSimulation)
				return null;
			UserActionSimulator simulator = userSimulatorsItr.next();
			long currentTime = getCurrentTime();
			simulator.setTime(currentTime);
			simulator.setTransactionID(transactionID++);
			return simulator;
		}
	}

	private void emptyQueueProcedure() {
		if (endOfSimulation)
			return;
		timeAdvanceProcedure();
		if (simulationDurationInHoursRemaining > 0)
			userSimulatorsItr = userSimulators.iterator();
		else {
			endOfSimulationProcedure();
		}
	}

	private void endOfSimulationProcedure() {
		endOfSimulation = true;
		endOfSimulationCondition.release();
	}

	private void timeAdvanceProcedure() {
		incrementTime(TimeUnit.HOURS.toMillis(1));
		simulationDurationInHoursRemaining--;
	}

	public void simulate() {
		int concurrencyLevel = Integer.parseInt(properties.getProperty("concurrencyLevel"));
		Thread[] workers = new Thread[concurrencyLevel];
		for (int i = 0; i < concurrencyLevel; i++) {
			Thread x = new Thread(new userSimuAgent(this));
			workers[i] = x;
			x.start();
		}
		Thread monitoring = new Thread(new Runnable() {

			@Override
			public void run() {
				long startTime = System.currentTimeMillis();
				boolean end = false;
				while (!end) {
					try {
						Thread.sleep(monitoringCheckInterval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					end = simulationDurationInHoursRemaining == 0;
					float progress = (simulationDurationInHours - simulationDurationInHoursRemaining)
							/ (float) simulationDurationInHours * 100.0f;
					BigDecimal bd = new BigDecimal(Float.toString(progress));
					int totalLogSize = 0;
					for(HDFSReporter reporter: HDFSReporter.reporters) {
						totalLogSize += reporter.getTotalLogSize();
					}
						
					System.out.println(dateFormat.format(new Date(getCurrentTime())) + " == Progress "
							+ bd.setScale(3, BigDecimal.ROUND_HALF_UP) + " == Log Size "
							+ (totalLogSize / 1024 / 1024) + " MB" + " == Avg. Log Writing Speed "
							+ (totalLogSize / 1024.0f / 1024.0f / ((System.currentTimeMillis() - startTime) / 1000.0f)) + " MB/s");
				}
			}
		});
		monitoring.start();
		for (Thread worker : workers) {
			try {
				worker.join();
				System.out.println("************" + worker.getState() + "***********");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		for(HDFSReporter reporter: HDFSReporter.reporters) {
			reporter.kill();
		}
		// try {
		// endOfSimulationCondition.acquire();
		// endOfSimulationCondition.acquire();
		// Thread.sleep(5000);
		// reporter.kill();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
	}

	private static class userSimuAgent implements Runnable {
		private EntitiesSimulator master;

		public userSimuAgent(EntitiesSimulator master) {
			this.master = master;
		}

		@Override
		public void run() {
			while (true) {
				UserActionSimulator simulator = master.getUserSimulator();
				if (simulator == null)
					break;
				simulator.simulateUsersActions();
			}
		}
	}
}
