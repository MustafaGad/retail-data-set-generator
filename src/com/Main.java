package com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.entities.Brand;
import com.entities.Category;
import com.entities.EntityContainer;
import com.entities.Location;
import com.entities.User;
import com.mathUtilities.NormalDistribution;
import com.mathUtilities.ProbabilityDistribution;
import com.mathUtilities.RandomExperiment;
import com.mathUtilities.RandomExperimentFactory;
import com.mathUtilities.UniformDistribution;
import com.mathUtilities.Utilities;
import com.simulators.EntitiesSimulator;

public class Main {
	
	public static Properties properties;
	public static void main(String [] args) throws FileNotFoundException, IOException, InterruptedException {
		
		long start = System.currentTimeMillis();
		initializeProperties(args[0]);
		initializeEntities();
//		EntityContainer.dumpEntities();
		HDFSReporter.initReporters(Integer.parseInt(properties.getProperty("reportersCount")), properties.getProperty("HDFSLogPath"), Long.parseLong(properties.getProperty("logBatchSize")), Boolean.parseBoolean(properties.getProperty("binaryOutput")));
		Thread simulatorThread = new Thread(new EntitySimulator());
		simulatorThread.start();
		simulatorThread.join();
		long duration = System.currentTimeMillis() - start;
		System.out.println("Simulation Duration " + duration / 1000.0f / 60.0f );
	}

	private static void initializeProperties(String propertiesPath) {
		properties = new Properties();
		try {
			properties.load(new FileInputStream(propertiesPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void initializeEntities() {
		setRandomnessSeed();
		initLocations();
		initUsers();
		initCategories();
		initBrands();
		initProducts();
//		initStores();
	}
	
	
	private static void setRandomnessSeed() {
		Utilities.setSeed(Long.parseLong(properties.getProperty("randomnessSeed")));
	}

	private static void initBrands() {
		try {
			String brandsPath = properties.getProperty("brandsPath");
			BufferedReader reader = new BufferedReader(new FileReader(new File(brandsPath)));
			String line;
			HashMap<Short, Brand> brands = new HashMap<>();
			while((line = reader.readLine()) != null) {
				String [] temp = line.split(",");
				Short brandID = Short.parseShort(temp[0]);
				String brandName = temp[1];
				brands.put(brandID, new Brand(brandID, brandName));
			}
			reader.close();
			EntityContainer.setBrands(brands);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void initCategories() {
		try {
			String categoriesPath = properties.getProperty("categoriesPath");
			BufferedReader reader = new BufferedReader(new FileReader(new File(categoriesPath)));
			String line;
			HashMap<Short, Category> categories = new HashMap<>();
			while((line = reader.readLine()) != null) {
				String [] temp = line.split(",");
				Short categoryID = Short.parseShort(temp[0]);
				Short parentCategoryID = Short.parseShort(temp[1]);
				Category parentCategory;
				Category category;
				
				String parentCategoryName = null;
				if(temp.length == 4)
					parentCategoryName = temp[3];
				if(categories.containsKey(parentCategoryID)) {
					parentCategory = categories.get(parentCategoryID);
				} else {
					parentCategory = new Category(parentCategoryID, null, parentCategoryName);
					categories.put(parentCategoryID, parentCategory);
				}
				String categoryName = null;
				if(temp.length >= 3)
					categoryName = temp[2];
				
				if(categories.containsKey(categoryID)) {
					category = categories.get(categoryID);
					category.setParentCategory(parentCategory);
				}
				else {
					category = new Category(categoryID, parentCategory, categoryName);
					categories.put(categoryID,category);
				}
			}
			reader.close();
			EntityContainer.setCategories(categories);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void initLocations() {
		try {
			String locationsPath = properties.getProperty("locationsPath");
			BufferedReader reader = new BufferedReader(new FileReader(new File(locationsPath)));
			String line;
			short id = 0;
			while((line = reader.readLine()) != null) {
				String[] raw = line.split(",");
				EntityContainer.addLocation(new Location(id++, raw[0], raw[1], raw[2], Float.parseFloat(raw[3]), Float.parseFloat(raw[4])));
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	private static void initStores() {
		// TODO Auto-generated method stub
		
	}

	private static void initProducts() {
		try {
			String productsInitPropertiesPath = properties.getProperty("productsInitPropertiesPath");
			Properties productsProp = new Properties();
			productsProp.load(new FileInputStream(new File(productsInitPropertiesPath)));
			
			int productsCount = Integer.parseInt(productsProp.getProperty("productsCount"));
			RandomExperiment<Short> brandsRE = initBrandRE(productsProp);
			RandomExperiment<Short> categoriesRE = initCategoriesRE(productsProp);
			String priceDistrRaw = productsProp.getProperty("priceDistr");
			String [] priceDistr = priceDistrRaw.split("-");
			ProbabilityDistribution priceProbDistr = null;
			if(priceDistr[0].compareTo("N") == 0) {
				float mean = Float.parseFloat(priceDistr[1]);
				float stdv = Float.parseFloat(priceDistr[2]);
				float minValue = Float.parseFloat(priceDistr[3]);
				float maxValue = Float.parseFloat(priceDistr[4]);
				
				priceProbDistr = new NormalDistribution(mean, stdv, minValue, maxValue);
			} else { //Uniform
				float minValue = Float.parseFloat(priceDistr[1]);
				float maxValue = Float.parseFloat(priceDistr[2]);
				
				priceProbDistr = new UniformDistribution(minValue, maxValue);
			}
			for(int i=0; i<productsCount; i++) {
				Category productCategory = EntityContainer.getCategory(categoriesRE.nextRand());
				Brand productBrand = EntityContainer.getBrand(brandsRE.nextRand());
				float productPrice = priceProbDistr.nextSample();
				
				EntityContainer.addProduct(new com.entities.Product(i, productBrand, productCategory, productPrice));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static RandomExperiment<Short> initCategoriesRE(Properties productsProp) {
		String categoriesDistrRaw = productsProp.getProperty("categoriesDistr");
		HashMap<Short, Float> distrMap = new HashMap<Short, Float>();
		String[] distrRaw = categoriesDistrRaw.split("-");
		for(int i = 0; i < distrRaw.length; i++) {
			String[] pairRaw = distrRaw[i].split(",");
			distrMap.put(Short.parseShort(pairRaw[0]), Float.parseFloat(pairRaw[1]));
		}

		return (RandomExperiment<Short>) RandomExperimentFactory.getRandomExperiment(distrMap);
	}

	private static RandomExperiment<Short> initBrandRE(Properties properties) {
		String brandsDistrRaw = properties.getProperty("brandsDistr");
		HashMap<Short, Float> distrMap = new HashMap<Short, Float>();
		String[] distrRaw = brandsDistrRaw.split("-");
		for(int i = 0; i < distrRaw.length; i++) {
			String[] pairRaw = distrRaw[i].split(",");
			distrMap.put(Short.parseShort(pairRaw[0]), Float.parseFloat(pairRaw[1]));
		}

		return (RandomExperiment<Short>) RandomExperimentFactory.getRandomExperiment(distrMap);
	}

	private static void initUsers() {
		int usersCount = Integer.parseInt(properties.getProperty("usersCount"));
		float usersMaleRatio = Float.parseFloat(properties.getProperty("usersMaleRatio"));
		RandomExperiment<Boolean> userGenderExperiment = RandomExperimentFactory.getRandomExperiment(usersMaleRatio);
		
		float [][] usersAgeDistr = calcUserAgeDistr();
		RandomExperiment<Float> usersAgeExperiment = RandomExperimentFactory.getRandomExperiment(usersAgeDistr);
		
		HashMap<Short, Float> usersLocationsDistr = calcUsersLocationsDistr();
		RandomExperiment<Short> usersLocationExperiment = (RandomExperiment<Short>) RandomExperimentFactory.getRandomExperiment(usersLocationsDistr);
		
		User.userBehaviourSegMatrix = calcUserBehavSegMatrix();
		float [] usersBehavSegDistr = calcUsersBehavSegDistr();
		RandomExperiment<Integer> userBehavExperiment = RandomExperimentFactory.getRandomExperiment(usersBehavSegDistr);
		int id = 0;
		ArrayList<User> users = new ArrayList<User>(usersCount);
		for(int i=0; i < usersCount; i++) {
			
			User u = new User(id++, userGenderExperiment.nextRand(), usersAgeExperiment.nextRand(), EntityContainer.getLocation(usersLocationExperiment.nextRand()), User.userBehaviourSegMatrix[userBehavExperiment.nextRand()]);
			users.add(u);
			EntityContainer.setUsers(users);
		}
		
	}


	private static float[][] calcUserBehavSegMatrix() {
		String usersBehavSeg = properties.getProperty("userBehavSeg");
		String [] behavSegRaw = usersBehavSeg.split("-");
		
		float[][] usersBehavSegMatrix = new float[behavSegRaw.length][User.BEHAV_FEATURE_COUNT];
		for(int i = 0; i < usersBehavSegMatrix.length; i++) {
			String[] temp = behavSegRaw[i].split(",");
			for(int j=0; j < User.BEHAV_FEATURE_COUNT; j++) {
				usersBehavSegMatrix[i][j] = Float.parseFloat(temp[j]);
			}
		}
		return usersBehavSegMatrix;
	}

	private static float[] calcUsersBehavSegDistr() {
		String usersBehavSegDistrRaw = properties.getProperty("usersBehavSegDistr");
		String[] distrRaw = usersBehavSegDistrRaw.split(",");
		float [] distr = new float[distrRaw.length];
		for(int i = 0; i < distrRaw.length; i++) {
			distr[i] = Float.parseFloat(distrRaw[i]);
		}
		return distr;
	}
	
	private static HashMap<Short, Float> calcUsersLocationsDistr() {
		String locationsDistrRaw = properties.getProperty("locationsDistr");
		HashMap<Short, Float> distrMap = new HashMap<Short, Float>();
		String[] distrRaw = locationsDistrRaw.split("-");
		for(int i = 0; i < distrRaw.length; i++) {
			String[] pairRaw = distrRaw[i].split(",");
			distrMap.put(Short.parseShort(pairRaw[0]), Float.parseFloat(pairRaw[1]));
		}
		return distrMap;
	}

	private static float[][] calcUserAgeDistr() {
		String userAgeDistrRaw = properties.getProperty("usersAgeDistr");
		String [] ageBucketsRaw = userAgeDistrRaw.split("-");
		int startAge = Integer.parseInt(ageBucketsRaw[0]);
		float bucketLength = Float.parseFloat(ageBucketsRaw[1]);
		int bucketCount = Integer.parseInt(ageBucketsRaw[2]);
		String [] probsRaw = ageBucketsRaw[3].split(",");
		float [] probs = new float [probsRaw.length];
		for(int i = 0; i < probs.length; i++) {
			probs[i] = Float.parseFloat(probsRaw[i]);
		}
		float [][] distr = new float [bucketCount + 1] [2];
		for(int i = 0; i < bucketCount; i++) {
			distr[i][0] = startAge + i * bucketLength;
			distr[i][1] = probs[i];
		}
		
		distr[bucketCount][0] = startAge + bucketCount * bucketLength;
		distr[bucketCount][1] = -1;
		
		//distr[i][0] --> bucket i start point.
		//distr[i][1] --> bucket i probability.
		//distr[bucketCount][0] --> bucket i-1 end point.
		//distr[bucketCount][1] --> dumy value should be neglected on parsing.
		return distr;
	}

	private static class EntitySimulator implements Runnable{

		@Override
		public void run() {
			EntitiesSimulator simulator = new EntitiesSimulator(properties, Long.parseLong(properties.getProperty("simulationDuration")), TimeUnit.HOURS);
			simulator.simulate();			
		}
		
	}


}
