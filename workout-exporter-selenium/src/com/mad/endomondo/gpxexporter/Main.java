package com.mad.endomondo.gpxexporter;


public class Main {

	public static String FB_LOGIN = "login";
	public static String FB_PASS = "pass";

	public static void main(String[] args) {
		Exporter exporter = new Exporter(FB_LOGIN, FB_PASS);
		//exporter.doExport();
		
		exporter.exportWorkoutList();
	}

}
