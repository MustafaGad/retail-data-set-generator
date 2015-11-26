package com.simulators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.HDFSReporter;
import com.entities.EntityContainer;
import com.entities.Product;
import com.entities.User;
import com.mathUtilities.NormalDistribution;
import com.mathUtilities.RandomExperiment;
import com.mathUtilities.RandomExperimentFactory;

public class UserActionSimulator {

	private HashMap<Integer, Product> products;
	private User user;
	private RandomExperiment<Boolean> visitExperiment;
	private RandomExperiment<Boolean> viewExperiment;
	private RandomExperiment<Boolean> addToCartExperiment;
	private RandomExperiment<Boolean> checkOutExperiment;
	private RandomExperiment<Boolean> paymentExperiment;
	private RandomExperiment<Boolean> salesExperiment;
	private NormalDistribution viewedProductsCountDistr;
	public static RandomExperiment<Integer> productsViewedExperiment;
	private long timeStamp;
	private int transactionID;

	public UserActionSimulator(User user) {
		products = EntityContainer.getProducts();
		this.user = user;
		visitExperiment = RandomExperimentFactory.getRandomExperiment(user.getVisitProb());
		viewExperiment = RandomExperimentFactory.getRandomExperiment(user.getViewProb());
		addToCartExperiment = RandomExperimentFactory.getRandomExperiment(user.getAddToCartProb());
		checkOutExperiment = RandomExperimentFactory.getRandomExperiment(user.getCheckoutProb());
		paymentExperiment = RandomExperimentFactory.getRandomExperiment(user.getInitPaymentProb());
		salesExperiment = RandomExperimentFactory.getRandomExperiment(user.getFinishPaymentProb());
		viewedProductsCountDistr = new NormalDistribution(user.getMeanProductViewCount(),
				user.getStdvProductViewCount(), 0, products.size());
		if(productsViewedExperiment == null) {
			HashMap<Integer, Float> map = new HashMap<Integer, Float>();
			Set<Integer> keys = products.keySet();
			int productsCount = products.size();
			for(Integer key: keys) {
				map.put(key, 1.0f / productsCount);
			}
			productsViewedExperiment = (RandomExperiment<Integer>) RandomExperimentFactory.getRandomExperiment(map);
		}
	}
	
	public void setTime(long time) {
		this.timeStamp = time; 
	}
	public void simulateUsersActions() {
		boolean visit = evalVisit(user);
		if (!visit) {
			return;
		}
		HDFSReporter.log(new Action(transactionID, timeStamp, Action.Subject.SHOPPER, user.getId(), Action.ActionType.VISIT));

		List<Product> viewedProducts = evalViewedProducts(user);
		if (viewedProducts == null) {
			return;
		}
		for(Product p: viewedProducts) {
			HDFSReporter.log(new Action(transactionID, timeStamp, Action.Subject.SHOPPER, user.getId(), Action.ActionType.VIEW, p.getId()));
		}

		List<Product> addedToCartProducts = evalCart(user, viewedProducts);
		if (addedToCartProducts == null) {
			return;
		}
		for(Product p: addedToCartProducts) {
			HDFSReporter.log(new Action(transactionID, timeStamp, Action.Subject.SHOPPER, user.getId(), Action.ActionType.ADD_TO_CART, p.getId()));
		}
		boolean checkout = evalCheckout(user);
		if (!checkout) {
			return;
		}
		HDFSReporter.log(new Action(transactionID, timeStamp, Action.Subject.SHOPPER, user.getId(), Action.ActionType.CHECKOUT));

		boolean payment = evalPayment(user);
		if (!payment) {
			return;
		}
		HDFSReporter.log(new Action(transactionID, timeStamp, Action.Subject.SHOPPER, user.getId(), Action.ActionType.INIT_PAYMENT));
		
		boolean sold = evalSale();
		if(!sold)
			return;
		for(Product p: addedToCartProducts) {
			HDFSReporter.log(new Action(transactionID, timeStamp, Action.Subject.SHOPPER, user.getId(), Action.ActionType.BOUGHT, p.getId()));
		}
	}

	private boolean evalSale() {
		return salesExperiment.nextRand();
	}

	public static void main(String[] args) {

	}

	private boolean evalPayment(User u) {
		return paymentExperiment.nextRand();
	}

	private boolean evalCheckout(User u) {
		return checkOutExperiment.nextRand();
	}

	private List<Product> evalCart(User u, List<Product> viewedProducts) {
		ArrayList<Product> addedToCart = new ArrayList<Product>(viewedProducts.size());
		for(Product viewed: viewedProducts) {
			if(addToCartExperiment.nextRand())
				addedToCart.add(viewed);
		}
		if(addedToCart.isEmpty())
			return null;
		return addedToCart;
	}

	private List<Product> evalViewedProducts(User u) {
		if (!viewExperiment.nextRand())
			return null;
		Product[] viewed = new Product[(int) viewedProductsCountDistr.nextSample()];
		if(viewed.length == 0)
			return null;
		for (int i = 0; i < viewed.length; i++) {
			
			viewed[i] = products.get(productsViewedRandomSample());
		}
		return Arrays.asList(viewed);
	}
	
	public synchronized static Integer productsViewedRandomSample() {
		return productsViewedExperiment.nextRand();
	}
	private boolean evalVisit(User u) {
		return visitExperiment.nextRand();
	}

	public void setTransactionID(int transactionID) {
		this.transactionID = transactionID;
	}

}
