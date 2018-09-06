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

    public void fillLoginDetails() throws Exception
    {
        if (!Context.get().getAccount().isInitialized())
        {
            throw new Exception("The provided ebay-kleinanzeigen account credentials are invalid.");
        }

        $("#login-email").shouldBe(visible).scrollTo().val(Context.get().getAccount().username);
        $("#login-password").shouldBe(visible).scrollTo().val(Context.get().getAccount().password);
    }

    public Homepage clickLogin()
    {
        $("#login-submit").scrollTo().shouldBe(visible).click();

        header.validateIsLoggedIn();

        return new Homepage();
    }
}
