package com.simulators;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.entities.User;

public class UnNamed {

	private List<User> users;
	private Iterator<User> itr;
	private Lock lock;
	private Condition empty = lock.newCondition();
	private Condition ready = lock.newCondition();

	public UnNamed(List<User> users) {
		this.users = users;
		lock = new ReentrantLock(true);
	}

	private synchronized User getUser() {
		try {
			if (!itr.hasNext()) {
				empty.signal();
			}
			do {
				ready.await();
			} while (!itr.hasNext());
			return itr.next();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	private synchronized void resetUserPool() {
			try {
			while (itr.hasNext()) {
				empty.await();
			}
			itr = users.iterator();
			ready.signalAll();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}
