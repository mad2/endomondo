package com.mad.endomondo.gpxexporter.command;

import java.util.List;

import org.joda.time.LocalDate;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mad.commons.RetryExcutor;
import com.mad.commons.RetryableCommand;
import com.mad.endomondo.gpxexporter.FindElementModule;
import com.mad.endomondo.gpxexporter.exception.ExporterException;

public class ExportDayCommand implements RetryableCommand<LocalDate, String> {

	private WebDriver driver;
	private FindElementModule findElementModule;

	public ExportDayCommand(WebDriver driver,
			FindElementModule findElementModule) {
		super();
		this.driver = driver;
		this.findElementModule = findElementModule;
	}

	@Override
	public String execute(LocalDate d) {
		String iconsXPath = "//td[contains(concat(' ',normalize-space(@class),' '),' in-month')]//span[contains(concat(' ',normalize-space(@class),' '),' cday')][text()=\""
				+ d.getDayOfMonth() + "\"]/..//a";
		List<WebElement> icons = driver.findElements(By.xpath(iconsXPath));
		int iconsCount = icons.size();
		icons = null; // can't iterate over that -
						// org.openqa.selenium.StaleElementReferenceException:
						// Element is no longer attached to the DOM

		for (int i = 0; i < iconsCount; i++) {
			String iconXpath = "(" + iconsXPath + ")[" + (i + 1) + "]";
			WebElement icon = driver.findElement(By.xpath(iconXpath));

			System.out.println(icon.getAttribute("id"));

			System.out.println("Clicking icon");
			icon.click();

			// Hover
			WebElement more = driver
					.findElement(By
							.xpath("//li[contains(concat(' ',normalize-space(@class),' '),' dropdown')]"));

			new Actions(driver).moveToElement(more).build().perform();

			WebDriverWait wait = new WebDriverWait(driver, 30);
			WebElement element = wait
					.until(ExpectedConditions.visibilityOfElementLocated(By
							.xpath("//a[contains(concat(' ',normalize-space(@class),' '),' export')]")));

			System.out.println("Clicking export button");
			element.click();

			// Wait for javascript to render the popup
			findElementModule.fluentWait(By.xpath("//div[@id='exporter']"));

			String tcxGpx = "id('lightboxContainer')//td//a";
			List<WebElement> exportLinks = driver
					.findElements(By.xpath(tcxGpx));

			System.out.println("Export tcx");
			exportLinks.get(0).click();
			System.out.println("Export gpx");
			exportLinks.get(1).click();

			RetryableCommand<Object, Object> closeExportPopupCommand = new CloseExportPopupCommand(
					driver);
			RetryExcutor<Object, Object> executor = new RetryExcutor<>();
			try {
				executor.executeWithRetries(10, closeExportPopupCommand, null);
			} catch (Exception e) {
				throw new ExporterException(
						"Error closing export popup. Too many retries. Date="
								+ d, e);
			}

		}

		if (iconsCount == 0) {
			System.out.println("No workouts.");
		}

		return null;
	}

}
