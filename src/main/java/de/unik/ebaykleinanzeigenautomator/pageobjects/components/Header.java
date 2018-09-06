package de.unik.ebaykleinanzeigenautomator.pageobjects.components;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;

import de.unik.ebaykleinanzeigenautomator.pageobjects.Component;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.Homepage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.LoginPage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.LogoutPage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.ManagedAdsPage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.PostAdSelectCategoryPage;

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
        $("#site-logo > .logo > a").should(exist).scrollTo().shouldBe(visible).click();

        return new Homepage();
    }

    public LoginPage clickLoginLink()
    {
        loginLink.scrollTo().shouldBe(visible).click();

        return new LoginPage();
    }

    public LogoutPage clickLogoutLink()
    {
        logoutLink.scrollTo().shouldBe(visible).click();

        validateIsLoggedOut();

        return new LogoutPage();
    }

    public PostAdSelectCategoryPage clickPostAd()
    {
        $("#site-mainnav-postad-link").scrollTo().shouldBe(visible).click();

        return new PostAdSelectCategoryPage();
    }

    public ManagedAdsPage clickManagedAds()
    {
        $("#site-mainnav-my-link").scrollTo().shouldBe(visible).click();

        $("#my-manageads-content").shouldBe(visible);

        return new ManagedAdsPage();
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
