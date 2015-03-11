package com.mad.play;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

public class AutoDownloadFiles {

	public static void main(String[] args) {
		FirefoxProfile profile = new FirefoxProfile();

		profile.setPreference("browser.download.dir", "/home/mad/workspace/endomondo/workout-exporter-selenium/export");
		profile.setPreference("browser.download.folderList", 2);
		profile.setPreference("browser.download.manager.showWhenStarting", false);
		profile.setPreference("browser.helperApps.alwaysAsk.force", false);
		profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/octet-stream");

		WebDriver driver = new FirefoxDriver(profile);	
		
		driver.get("http://garmintrainer.googlecode.com/hg/src/main/resources/sample.tcx");
	}
	
	public static void main2(String[] args) {
		FirefoxProfile profile = new FirefoxProfile();

		profile.setPreference("browser.download.dir", "/home/mad/workspace/endomondo/workout-exporter-selenium/export");
		profile.setPreference("browser.download.folderList", 2);
		profile.setPreference("browser.download.manager.showWhenStarting", false);
		profile.setPreference("browser.helperApps.alwaysAsk.force", false);
		profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/zip");

		WebDriver driver = new FirefoxDriver(profile);	
		
		driver.get("http://www.vbaccelerator.com/home/VB/Code/vbMedia/Audio/Lossless_WAV_Compression/Sample_APE_File.zip");
	}
}
