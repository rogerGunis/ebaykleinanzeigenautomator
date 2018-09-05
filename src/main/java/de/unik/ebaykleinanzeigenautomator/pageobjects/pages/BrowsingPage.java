package de.unik.ebaykleinanzeigenautomator.pageobjects.pages;

import com.codeborne.selenide.Selenide;

import de.unik.ebaykleinanzeigenautomator.pageobjects.PageObject;
import de.unik.ebaykleinanzeigenautomator.pageobjects.components.Header;
import de.unik.ebaykleinanzeigenautomator.util.Context;

public class BrowsingPage extends PageObject
{
    public Header header = new Header();

    public BrowsingPage()
    {
        validateIsExpectedPage();
    }

    @Override
    public void validateIsExpectedPage()
    {
        // With each new page we wait a little to minimize risk of account lock due to automation
        Selenide.sleep(Context.get().getConfiguration().projectGeneralDelay());

        header.validateComponentIsAvailable();
    }
}
