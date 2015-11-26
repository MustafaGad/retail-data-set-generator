package com;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.mathUtilities.Utilities;
import com.simulators.Action;

public class HDFSReporter {
	private BufferedWriter writer;
	private ObjectOutputStream objOutStr;
	private FileSystem fileSystem;
	private Semaphore emptyQueue;
	private long logCount = 0;
	private long logSize;
	private long logBatchSize;
	private static AtomicInteger logBatchIndex = new AtomicInteger(0);
	private String HDFSPath;
	private long totalLogSize;
	private boolean binary;
	private BufferedOutputStream bufferedStream;
	public static HDFSReporter [] reporters;
	private static Random random;
	public HDFSReporter(String HDFSPath, long logBatchSize, boolean binary) {
		try {
			this.logBatchSize = logBatchSize;
			emptyQueue = new Semaphore(1);
			emptyQueue.acquire();
			this.HDFSPath = HDFSPath;
			this.binary = binary;
			initWriter(binary);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	public static void initReporters(int reportersCount, String HDFSPath, long logBatchSize, boolean binary) {
		random = new Random(Utilities.getSeed());
		reporters = new HDFSReporter[reportersCount];
		for(int i = 0; i < reportersCount; i++) {
			reporters[i] = new HDFSReporter(HDFSPath, logBatchSize, binary);
		}
	}
	private void initWriter(boolean binary) {
		try {
			fileSystem = FileSystem.get(new URI(HDFSPath), new Configuration());
			if (binary) {
				bufferedStream = new BufferedOutputStream(
						fileSystem.create(new Path(HDFSPath + "_" + logBatchIndex.getAndIncrement())));
			} else
				writer = new BufferedWriter(
						new OutputStreamWriter(fileSystem.create(new Path(HDFSPath + "_" + logBatchIndex.getAndIncrement()))));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}

	}

	private void updateWriter() {
		try {
			if (binary) {
				bufferedStream.flush();
				bufferedStream.close();
				bufferedStream = new BufferedOutputStream(
						fileSystem.create(new Path(HDFSPath + "_" + logBatchIndex.getAndIncrement())));
			} else {
				writer.flush();
				writer.close();
				writer = new BufferedWriter(
						new OutputStreamWriter(fileSystem.create(new Path(HDFSPath + "_" + logBatchIndex.getAndIncrement()))));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void kill() {
		try {
			System.out.println("Logged Messages: " + ++logCount);
			if(binary)
				bufferedStream.close();
			else
				writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private byte[] serialize(Serializable obj) {
		try {
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(byteStream);
			os.writeObject(obj);
			return byteStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void log(Action action) {
		HDFSReporter reporter;
		synchronized (random) {
			reporter = reporters[random.nextInt(reporters.length)];
		}
		reporter.logAction(action);
	}
	public synchronized void logAction(Action action) {
		
		if (binary) {
			byte[] bytes = serialize(action);
			try {
				if (logSize + bytes.length >= logBatchSize) {
					updateWriter();
					logSize = 0;
				}
				logSize += bytes.length;
				totalLogSize += bytes.length;
				logCount++;
				bufferedStream.write(bytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			String logMessage = action.toString() + "\n";
			try {
				if (logSize + logMessage.length() >= logBatchSize) {
					updateWriter();
					logSize = 0;
				}
				logSize += logMessage.length();
				totalLogSize += logMessage.length();
				logCount++;
				writer.write(logMessage);

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public long getTotalLogSize() {
		return totalLogSize;
	}
	// public static class HDFSDispatcher implements Runnable {
	// private HDFSReporter reporter;
	//
	// public HDFSDispatcher(HDFSReporter reporter) {
	// this.reporter = reporter;
	// }
	//
	// @Override
	// public void run() {
	// while (!reporter.stop) {
	// try {
	// reporter.writer.wait();
	// reporter.writer.write(reporter.passiveLogsContainer.toString());
	// reporter.passiveLogsContainer.delete(0,
	// reporter.passiveLogsContainer.length());
	// } catch (IOException | InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	// try {
	// reporter.writer.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// }
	// }

}
