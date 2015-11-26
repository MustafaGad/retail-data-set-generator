package com.simulators;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Action implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6566875571894974035L;
	private static transient SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");;
	public static enum Subject {
		SHOPPER, STORE;
	}
	
	public static enum ActionType {
		VISIT, VIEW, ADD_TO_CART, CHECKOUT, INIT_PAYMENT, BOUGHT;
	}
	
	private long timestamp;
	private Subject subject;
	private ActionType actionType;
	private int subjectID;
	private int productID;
	private int transactionID;
	
	public Action(int transactionID, long timestamp, Subject subject, int subjectID, ActionType actionType, int productID) {
		this.transactionID = transactionID;
		this.timestamp = timestamp;
		this.subject = subject;
		this.subjectID = subjectID;
		this.actionType = actionType;
		this.productID = productID;
	}
	public Action(int transactionID, long timestamp, Subject subject, int subjectID, ActionType actionType) {
		this.transactionID = transactionID;
		this.timestamp = timestamp;
		this.subject = subject;
		this.subjectID = subjectID;
		this.actionType = actionType;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public Subject getSubject() {
		return subject;
	}
	public ActionType getActionType() {
		return actionType;
	}
	public int getSubjectID() {
		return subjectID;
	}
	public int getProductID() {
		return productID;
	}
	@Override
	public String toString() {
		StringBuilder strB = new StringBuilder(100);
		strB.append(format.format(new Date(timestamp))).append("\t").append(transactionID).append("\t").append(subject).append("\t").append(subjectID).append("\t").append(actionType); 
		if(actionType == ActionType.VIEW || actionType == ActionType.ADD_TO_CART || actionType == ActionType.BOUGHT)
			strB.append("\t").append(productID);
		return strB.toString();
	}
}
