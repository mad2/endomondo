package com.mad.endomondo.gpxexporter.command;

import java.io.PrintWriter;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.mad.commons.RetryableCommand;

public class PrintWorkoutListCommand implements
		RetryableCommand<Object, Object> {

	private WebDriver driver;
	private int cols;
	private PrintWriter out;
	private StringBuilder buf;

	public PrintWorkoutListCommand(WebDriver driver, int cols, PrintWriter out) {
		super();
		this.driver = driver;
		this.cols = cols;
		this.out = out;
	}

	@Override
	public Object execute(Object input) {
		this.buf = new StringBuilder();

		List<WebElement> tableCells = driver
				.findElements(By
						.xpath("//tr[contains(concat(' ',normalize-space(@class),' '),' row')]/td/span"));
		int i = 0;
		for (WebElement webElement : tableCells) {
			String text = webElement.getText();
			System.out.print(text);
			System.out.print(" | ");

			if (text == null || text.isEmpty())
				buf.append("null");
			else
				buf.append(text);

			i++;
			if (i % cols == 0) {
				System.out.println("");
				buf.append("\n");
			} else {
				buf.append(",");
			}
		}

		out.print(buf.toString());

		return null;
	}

}
