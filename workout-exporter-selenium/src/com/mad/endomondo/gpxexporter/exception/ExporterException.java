package com.mad.endomondo.gpxexporter.exception;

public class ExporterException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ExporterException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExporterException(String message) {
		super(message);
	}

}
