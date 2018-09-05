package de.unik.ebaykleinanzeigenautomator.flows;

import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.Homepage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.LoginPage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.ManagedAdsPage;
import de.unik.ebaykleinanzeigenautomator.util.Context;

public class DeleteAllInactiveSmallAdsFlow
{
    public boolean run()
    {
        try
        {
            Homepage homepage = new OpenHomepageFlow().run();
            LoginPage loginPage = homepage.header.clickLoginLink();

            loginPage.fillLoginDetails();
            homepage = loginPage.clickLogin();

            ManagedAdsPage managedAdsPage = homepage.header.clickManagedAds();

            managedAdsPage.deleteAllInactiveSmallAds();

            managedAdsPage.header.clickLogoutLink();
        }
        catch (Throwable t)
        {
            System.out.println("Failed to delete all inactive small ads");
            System.out.println("Error was: " + t.getMessage());

            if (Context.get().getConfiguration().projectDebug())
            {
                t.printStackTrace();
            }

            return false;
        }

        return true;
    }
}
