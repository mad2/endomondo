package com.mad.commons;

public interface RetryableCommand<I,O> {
	O execute(I input);
}
