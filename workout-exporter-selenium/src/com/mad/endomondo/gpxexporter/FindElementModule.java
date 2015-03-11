package com.mad.endomondo.gpxexporter;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;

import com.google.common.base.Function;

public class FindElementModule {
	private WebDriver driver;

	public FindElementModule(WebDriver driver) {
		this.driver = driver;
	}

	public WebElement byTagAndClass(String tagName, String className) {
		WebElement element = driver.findElement(By.xpath("//" + tagName
				+ "[contains(concat(' ',normalize-space(@class),' '),' "
				+ className + "')]"));
		return element;

	}

	public WebElement byTextTagAndClass(String text, String tagName,
			String className) {

		WebElement element = driver.findElement(By.xpath("//" + tagName
				+ "[text()=\"" + text
				+ "\"][contains(concat(' ',normalize-space(@class),' '),' "
				+ className + "')]"));

		return element;
	}

	public WebElement byText(String text) {

		WebElement element = driver.findElement(By.xpath("//a[text()=\"" + text
				+ "\"]"));

		return element;
	}
	
	public WebElement fluentWait(final By locator) {
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver)
				.withTimeout(30, TimeUnit.SECONDS)
				.pollingEvery(5, TimeUnit.SECONDS)
				.ignoring(NoSuchElementException.class);

		WebElement foo = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(locator);
			}
		});

		return foo;
	};
}
