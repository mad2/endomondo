package com.mad.endomondo.gpxexporter;

import java.util.List;

import org.joda.time.LocalDate;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.mad.commons.RetryExcutor;
import com.mad.commons.RetryableCommand;
import com.mad.endomondo.gpxexporter.exception.ExporterException;

/**
 * //td[contains(concat(' ',normalize-space(@class),' '),'
 * in-month')]//span[contains(concat(' ',normalize-space(@class),' '),'
 * cday')][text()="1"]/..//a
 * 
 * @author mad
 * 
 */
public class WorkoutCalendarModule {

	private WebDriver driver;
	private List<String> monthNames;

	private FindElementModule find;

	public WorkoutCalendarModule(WebDriver driver, List<String> monthNames,
			FindElementModule find) {
		super();
		this.driver = driver;
		this.monthNames = monthNames;
		this.find = find;
	}

	public String readCurrentYear() {

		RetryableCommand<String, String> getCurrentYearTextCommand = new RetryableCommand<String, String>() {

			@Override
			public String execute(String input) {

				WebElement currentYear = find.byTagAndClass("span", "year");
				String currentYearText = currentYear.getText();

				return currentYearText;
			}
		};

		RetryExcutor<String, String> executor = new RetryExcutor<>();

		try {
			return executor.executeWithRetries(10, getCurrentYearTextCommand,
					null);
		} catch (Exception e) {
			throw new ExporterException("currentYearText == null");
		}
	}

	public int readCurrentYearNum() {
		String currentYearText = readCurrentYear();
		return Integer.parseInt(currentYearText);
	}

	public String readCurrentMonthName(LocalDate currentDate) {
		int monthNum = currentDate.getMonthOfYear();
		return monthNames.get(monthNum - 1);
	}

	public String readCurrentMonthName() {

		WebElement currentMonth = driver
				.findElement(By
						.xpath("//li[contains(concat(' ',normalize-space(@class),' '),' current')]/a"));

		String currentMonthName = currentMonth.getText().toLowerCase();

		return currentMonthName;
	}

	public int readCurrentMonthNum() {
		String currentMonthName = readCurrentMonthName();
		int inx = monthNames.indexOf(currentMonthName);
		return inx + 1;
	}

	public String calculatePreviousMonthName(String currentMonthName) {
		String previousMonthName = null;

		int currentMonthInx = monthNames.indexOf(currentMonthName);
		if (currentMonthInx == 0) {
			previousMonthName = monthNames.get(monthNames.size() - 1);
		} else {
			previousMonthName = monthNames.get(currentMonthInx - 1);
		}

		return previousMonthName;
	}

	public LocalDate openPreviousMonth(LocalDate currentDate) {
		String previousMonthName = calculatePreviousMonthName(currentDate);

		WebElement previousMonth = null;

		String december = getLastMonthName(monthNames);
		if (december.equals(previousMonthName)) {
			previousMonth = find.byTextTagAndClass(previousMonthName, "a",
					"previous");
		} else {
			previousMonth = find.byText(previousMonthName);
		}

		previousMonth.click();

		return currentDate.minusMonths(1).dayOfMonth().withMaximumValue();
	}

	private String calculatePreviousMonthName(LocalDate currentDate) {
		int previousMonthNum = currentDate.minusMonths(1).monthOfYear().get();
		return monthNames.get(previousMonthNum - 1);
	}

	// TODO: DUPLICATION
	public void openPreviousMothOld() {
		String currentMonthName = readCurrentMonthName();
		String previousMonthName = calculatePreviousMonthName(currentMonthName);

		WebElement previousMonth = null;

		String december = getLastMonthName(monthNames);
		if (december.equals(previousMonthName)) {
			previousMonth = find.byTextTagAndClass(previousMonthName, "a",
					"previous");
		} else {
			previousMonth = find.byText(previousMonthName);
		}

		previousMonth.click();
	}

	private static String getLastMonthName(List<String> monthNames) {
		return monthNames.get(monthNames.size() - 1);
	}

	public LocalDate calculateLastDayInCurrentMonth() {
		int currentYear = readCurrentYearNum();
		int currentMonth = readCurrentMonthNum();

		LocalDate startOfMonth = new LocalDate(currentYear, currentMonth, 1);
		LocalDate endOfMonth = startOfMonth.dayOfMonth().withMaximumValue();

		return endOfMonth;
	}
}
