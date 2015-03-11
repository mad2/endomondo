package com.mad.commons;

public class RetryExcutor<I, O> {

	public O executeWithRetries(int maxRetries, RetryableCommand<I, O> command,
			I input) throws Exception {

		Exception lastCause = null;
		for (int i = 0; i < maxRetries; i++) {
			try {
				O output = command.execute(input);
				return output;
			} catch (Exception e) {
				lastCause = e;
				System.out.println("Exception=" + e.toString()
						+ ", Retrying count=" + i);
			}
		}

		throw lastCause;

	}
}
