package de.unik.ebaykleinanzeigenautomator.pageobjects.pages;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.$;

public class Homepage extends BrowsingPage
{
	public Homepage()
	{
		validateIsExpectedPage();
	}
	
	@Override
	public void validateIsExpectedPage()
	{
		super.validateIsExpectedPage();
		
		$("#home").should(exist);
	}
}
