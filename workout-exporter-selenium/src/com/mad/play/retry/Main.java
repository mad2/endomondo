package com.mad.play.retry;

import com.mad.commons.RetryExcutor;
import com.mad.commons.RetryableCommand;

public class Main {
	public static void main(String[] args) throws Exception {
		// test1();
		test2();
	}

	public static void test1() {
		for (int i = 0; i < 100; i++) {
			System.out.println("Attempt=" + i);
			myProgram();
		}
	}

	public static void test2() throws Exception {
		RetryableCommand<String, String> myCommand = new RetryableCommand<String, String>() {

			@Override
			public String execute(String input) {
				myProgram();
				return "";
			}
		};

		RetryExcutor<String, String> executor = new RetryExcutor<>();
		for (int i = 0; i < 100; i++) {
			executor.executeWithRetries(10, myCommand, "");
		}
	}

	public static void preWork() {
		System.out.println("preWork");
	}

	public static void myProgram() {
		preWork();
		work();
	}

	public static void work() {
		if (getRandomBoolean() == true) {
			System.out.println("Work exception");
			throw new RuntimeException("Random exception "
					+ System.currentTimeMillis());
		} else {
			System.out.println("Work OK");
		}
	}

	public static boolean getRandomBoolean() {
		return Math.random() < 0.5;
		// I tried another approaches here, still the same result
	}
}
