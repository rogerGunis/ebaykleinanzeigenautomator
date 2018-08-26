package de.unik.ebaykleinanzeigenautomator.pageobjects.pages;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import de.unik.ebaykleinanzeigenautomator.util.Context;

public class LoginPage extends BrowsingPage
{
	@Override
	public void validateIsExpectedPage()
	{
		super.validateIsExpectedPage();
		
		$("#login").should(exist);
	}
	
	public void fillLoginDetails()
	{
		$("#login-email").shouldBe(visible).scrollTo().val(Context.get().getConfiguration().accountUsername());
		$("#login-password").shouldBe(visible).scrollTo().val(Context.get().getConfiguration().accountPassword());
	}
	
	public Homepage clickLogin()
	{
		$("#login-submit").shouldBe(visible).scrollTo().click();
		
		header.validateIsLoggedIn();
		
		return new Homepage();
	}
}
