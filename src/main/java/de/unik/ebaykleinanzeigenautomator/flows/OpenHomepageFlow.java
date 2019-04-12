package de.unik.ebaykleinanzeigenautomator.flows;

import static com.codeborne.selenide.Selenide.clearBrowserCookies;
import static com.codeborne.selenide.Selenide.open;

import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.Homepage;
import de.unik.ebaykleinanzeigenautomator.util.Context;

public class OpenHomepageFlow
{
    public Homepage run()
    {
    	// This mini flow will only be used by other flows, so we do not handle error scenarios here but in the calling flows
        clearBrowserCookies();

        open(Context.get().getConfiguration().siteUrl());

        return new Homepage();
    }
}
