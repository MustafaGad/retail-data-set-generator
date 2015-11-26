package com.simulators;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class DataSetSimulator {
	
	private static final int rateCheckInterval = 10;
	private String dataSetDirectory;
	private String simulationDirectory;
	private int megaBytesPerMinute;
	private Comparator<FileStatus> comparator;
	private long bytesServed;
	
	public DataSetSimulator(String dataSetDirectory, String simulationDirectory, int megaBytesPerMinute) {
		this(dataSetDirectory, simulationDirectory, megaBytesPerMinute, null);
	}
	public DataSetSimulator(String dataSetDirectory, String simulationDirectory, int megaBytesPerMinute, Comparator<FileStatus> comparator) {
		this.dataSetDirectory = dataSetDirectory;
		this.simulationDirectory = simulationDirectory;
		this.megaBytesPerMinute = megaBytesPerMinute;
		if(comparator == null)
			this.comparator = new Comparator<FileStatus>() {

				@Override
				public int compare(FileStatus file1, FileStatus file2) {
					return Integer.parseInt(file1.getPath().getName()) - Integer.parseInt(file2.getPath().getName());
				}
			};
		else		
			this.comparator = comparator;
		
	}
	public void run() {
		try {
			FileSystem fs = FileSystem.get(new URI(dataSetDirectory), new Configuration());
			FileStatus[] list = fs.listStatus(new Path(dataSetDirectory));
			fs.mkdirs(new Path(simulationDirectory));
			
			long startTimer = System.currentTimeMillis() - 10; // subtraction to avoid deletion by zero at first "currentRateCheck"
			bytesServed = 0;
			float currentRate = 0.0f;
			float desiredRate = megaBytesPerMinute * 1024 * 1024 / (float)(60 * 1000);
			Arrays.sort(list, comparator);
			for(FileStatus file: list) {
				if(file.isDirectory())
					continue;
				currentRate = bytesServed / (float)(System.currentTimeMillis() - startTimer);
				while(currentRate > desiredRate) {
					Thread.sleep(rateCheckInterval);
					currentRate = bytesServed / (float)(System.currentTimeMillis() - startTimer);
				}
				long length = file.getLen();
				fs.rename(file.getPath(), new Path(simulationDirectory, file.getPath().getName()));
				bytesServed += length;
			}
			
		} catch (IOException | URISyntaxException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public long bytesServed() {
		return bytesServed;
	}
	public static void main (String [] args) {
		args = new String[]{"hdfs://localhost:9000/user/Gad/dataSet", "hdfs://localhost:9000/user/Gad/simulationDataSet", "60"};
		DataSetSimulator simulator = new DataSetSimulator(args[0], args[1], Integer.parseInt(args[2]));
		long start = System.currentTimeMillis();
		simulator.run();
		float minutes = (System.currentTimeMillis() - start) / (1000.0f * 60.0f);
		System.out.println("Rate " + (simulator.bytesServed()/(1024.0f * 1024.0f)/minutes));
	}
}
