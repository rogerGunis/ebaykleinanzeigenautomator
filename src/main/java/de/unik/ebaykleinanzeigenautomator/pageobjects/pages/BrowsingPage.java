package de.unik.ebaykleinanzeigenautomator.pageobjects.pages;

import de.unik.ebaykleinanzeigenautomator.pageobjects.PageObject;
import de.unik.ebaykleinanzeigenautomator.pageobjects.components.Header;
import de.unik.ebaykleinanzeigenautomator.util.Context;
import de.unik.ebaykleinanzeigenautomator.util.Util;

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
        // With each posted ad wait a little to minimize account lock risk due to automation
        Util.waitOnPageLoad(Context.get().getConfiguration().projectGeneralDelay());
        
        header.validateComponentIsAvailable();
    }
}
