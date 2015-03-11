package com.mad.endomondo.gpxexporter.command;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.mad.commons.RetryableCommand;

public class CloseExportPopupCommand implements
		RetryableCommand<Object, Object> {

	private WebDriver driver;

	public CloseExportPopupCommand(WebDriver driver) {
		super();
		this.driver = driver;
	}

	@Override
	public Object execute(Object input) {
		WebElement closeButton = driver
				.findElement(By
						.xpath("//a[contains(concat(' ',normalize-space(@class),' '),' close')]"));
		System.out.println("Clicking close button");
		closeButton.click();

		return null;
	}

}
