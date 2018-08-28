package de.unik.ebaykleinanzeigenautomator.flows;

import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.Homepage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.LoginPage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.ManagedAdsPage;

public class ChangeStatusOfAllSmallAdsFlow
{
    private boolean activate;

    public ChangeStatusOfAllSmallAdsFlow(boolean activate)
    {
        this.activate = activate;
    }

    public void run()
    {
        Homepage homepage = new OpenHomepageFlow().run();
        LoginPage loginPage = homepage.header.clickLoginLink();

        loginPage.fillLoginDetails();
        homepage = loginPage.clickLogin();

        ManagedAdsPage managedAdsPage = homepage.header.clickManagedAds();

        if (activate)
        {
            managedAdsPage.activateAllSmallAds();
        }
        else
        {
            managedAdsPage.deactivateAllSmallAds();
        }

        managedAdsPage.header.clickLogoutLink();
    }
}
