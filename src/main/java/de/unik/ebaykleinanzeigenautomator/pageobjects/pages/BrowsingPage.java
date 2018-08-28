package de.unik.ebaykleinanzeigenautomator.pageobjects.pages;

import de.unik.ebaykleinanzeigenautomator.pageobjects.PageObject;
import de.unik.ebaykleinanzeigenautomator.pageobjects.components.Header;

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
        header.validateComponentIsAvailable();
    }
}
