package de.unik.ebaykleinanzeigenautomator.pageobjects.components;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;

import de.unik.ebaykleinanzeigenautomator.pageobjects.Component;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.Homepage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.LoginPage;

public class Header extends Component
{
	private SelenideElement loginLink = $("#site-signin nav li > a[data-gaevent*=LoginBegin]");
	
	private SelenideElement logoutLink = $("#user-logout");
	
	@Override
	public void validateComponentIsAvailable()
	{
		$("#site-header-top").should(exist);
	}
	
	public Homepage clickHome()
	{
		$("#site-logo > .logo > a").shouldBe(visible).scrollTo().click();
		
		return new Homepage();
	}
	
	public LoginPage clickLogin()
	{
		loginLink.shouldBe(visible).scrollTo().click();
		
		return new LoginPage();
	}
	
	public Homepage logout()
	{
		logoutLink.shouldBe(visible).scrollTo().click();
		
		validateIsLoggedOut();
		
		return new Homepage();
	}
	
	public void validateIsLoggedIn()
	{
		loginLink.shouldNotBe(visible);
		logoutLink.shouldBe(visible);
	}
	
	public void validateIsLoggedOut()
	{
		loginLink.shouldBe(visible);
		logoutLink.shouldNotBe(visible);
	}
}
