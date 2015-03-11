package com.mad.endomondo.gpxexporter;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import com.mad.commons.RetryExcutor;
import com.mad.commons.RetryableCommand;
import com.mad.endomondo.gpxexporter.command.ExportDayCommand;
import com.mad.endomondo.gpxexporter.command.PrintWorkoutListCommand;
import com.mad.endomondo.gpxexporter.exception.ExporterException;

public class Exporter {

	public static final List<String> monthNames = Arrays.<String> asList("sty",
			"lut", "mar", "kwi", "maj", "cze", "lip", "sie", "wrz", "paź",
			"lis", "gru");

	public static final LocalDate startDate = new LocalDate("2014-12-01");
	public static final LocalDate stopDate = new LocalDate("2013-12-30");

	private String fbLogin;
	private String fbPass;

	private WebDriver driver;

	private WorkoutCalendarModule workoutCal;
	private FindElementModule findElementModule;

	public Exporter(String fbLogin, String fbPass) {

		this.fbLogin = fbLogin;
		this.fbPass = fbPass;

		FirefoxProfile profile = createFirefoxProfile();

		driver = new FirefoxDriver(profile);

		System.out.println("Driver instantiated");

		initModules();

		System.out.println("Modules initialized");
		/* driver = new FirefoxDriver(); */
	}

	private void initModules() {
		findElementModule = new FindElementModule(driver);
		workoutCal = new WorkoutCalendarModule(driver, monthNames,
				findElementModule);

	}

	private FirefoxProfile createFirefoxProfile() {
		FirefoxProfile profile = new FirefoxProfile();

		profile.setPreference("browser.download.dir",
				"/home/mad/workspace/endomondo/workout-exporter-selenium/export/"
						+ System.currentTimeMillis());
		profile.setPreference("browser.download.folderList", 2);
		profile.setPreference("browser.download.manager.showWhenStarting",
				false);
		profile.setPreference("browser.helperApps.alwaysAsk.force", false);
		profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
				"application/tcx+xml, application/xml");

		return profile;
	}

	public void doExport() {
		System.out.println("Start");

		openWebSite();

		loginByFacebook(driver);

		LocalDate currentDate = workoutCal.calculateLastDayInCurrentMonth();

		// Find icons
		boolean stopYearEncountered = false;
		while (stopYearEncountered == false) {
			currentDate = exportCurrentMonth(currentDate, startDate);
			currentDate = workoutCal.openPreviousMonth(currentDate);

			stopYearEncountered = currentDate.isBefore(stopDate);
		}

		// webDriver.quit();

		System.out.println("End.");
	}

	public void exportWorkoutList() {
		System.out.println("openWebSite");
		driver.get("https://www.endomondo.com/workouts/list/");

		loginByFacebook(driver);

		int pageNum = 1;

		/* //a[@title='Idź do strony 2'] */
		/*
		 * //span[contains(concat(' ',normalize-space(@class),' '),'
		 * pages')]/a[text()='2']
		 */

		/*
		 * Export page
		 */
		// final int rows = 15;
		final int cols = 11;

		PrintWriter out = null;
		try {
			out = new PrintWriter("workout-list-"
					+ String.valueOf(System.currentTimeMillis()) + ".txt");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		while (true) {

			RetryExcutor<Object, Object> executor = new RetryExcutor<>();
			RetryableCommand<Object, Object> command = new PrintWorkoutListCommand(
					driver, cols, out);
			try {
				executor.executeWithRetries(10, command, null);
			} catch (Exception e) {
				throw new ExporterException("Error exporting workout table.", e);
			}

			/*
			 * Go to next page
			 */
			pageNum++;
			WebElement nextPageLink = null;
			try {
				nextPageLink = driver
						.findElement(By
								.xpath("//span[contains(concat(' ',normalize-space(@class),' '),' pages')]/a[text()='"
										+ pageNum + "']"));

			} catch (NoSuchElementException e) {
				System.out.println(e.toString());
			}

			if (nextPageLink == null) {
				System.out.println("No page with num=" + pageNum);
				break;
			}

			System.out.println("Opening page " + pageNum);
			nextPageLink.click();
			
			try {
				Thread.sleep(5000l);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		out.close();
		System.out.println("End");
	}

	private LocalDate exportCurrentMonth(LocalDate currentDate,
			LocalDate startDate) {
		LocalDate result = null;
		for (LocalDate d = currentDate; d.isAfter(currentDate.dayOfMonth()
				.withMinimumValue())
				|| d.isEqual(currentDate.dayOfMonth().withMinimumValue()); d = d
				.minusDays(1)) {

			result = d;

			if (currentDate.isAfter(startDate)) {
				System.out.println("Skipping. Current date=" + d
						+ ", Start date=" + startDate);
				continue;
			} else {
				System.out.println("Exporting date=" + d);
			}

			/*
			 * //td[contains(concat(' ',normalize-space(@class),' '),'
			 * in-month')]//span[contains(concat(' ',normalize-space(@class),'
			 * '),' cday')][text()="1"]/..//a
			 */

			RetryableCommand<LocalDate, String> exportDayCommand = new ExportDayCommand(
					driver, findElementModule);
			RetryExcutor<LocalDate, String> executor = new RetryExcutor<>();
			try {
				executor.executeWithRetries(10, exportDayCommand, d);
			} catch (Exception e) {
				throw new ExporterException(
						"Error printing icons. Too many retries.", e);
			}
		}
		return result;
	}

	private void openWebSite() {
		System.out.println("openWebSite");
		driver.get("https://www.endomondo.com/workouts/");
	}

	private void loginByFacebook(WebDriver driver) {
		System.out.println("Logging in...");

		// id = fbSignupBtn
		WebElement fbSignupBtn = driver.findElement(By.id("fbSignupBtn"));
		fbSignupBtn.click();

		// id = email < adamczuk@tlen.pl
		WebElement email = driver.findElement(By.id("email"));
		email.sendKeys(fbLogin);

		// id = pass < gmwgbtp
		WebElement pass = driver.findElement(By.id("pass"));
		pass.sendKeys(fbPass);

		// id = u_0_1 > click!
		WebElement fbLoginBtn = driver.findElement(By.id("u_0_1"));
		fbLoginBtn.click();
	}

	public static void main(String[] args) {
		// Exporter e = new Exporter("", "");

		WorkoutCalendarModule cal = new WorkoutCalendarModule(null, monthNames,
				null);
		System.out.println(cal.calculatePreviousMonthName("sie"));
		System.out.println(cal.calculatePreviousMonthName("sty"));
		System.out.println(cal.calculatePreviousMonthName("gru"));
	}
}
