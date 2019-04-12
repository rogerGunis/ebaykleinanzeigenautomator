package de.unik.ebaykleinanzeigenautomator.flows;

import de.unik.ebaykleinanzeigenautomator.datamodels.SmallAdContainer;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.Homepage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.LoginPage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.ManagedAdsPage;
import de.unik.ebaykleinanzeigenautomator.util.Context;

public class ExportSmallAdContainerFlow
{
    private SmallAdContainer smallAdContainer = null;

    public ExportSmallAdContainerFlow()
    {
        smallAdContainer = new SmallAdContainer();
    }

    public ExportSmallAdContainerFlow(SmallAdContainer smallAdContainer)
    {
        this.smallAdContainer = smallAdContainer;
    }

    public boolean run()
    {
        try
        {
            if (smallAdContainer == null)
            {
                throw new AssertionError("Small ad container was not initialized");
            }

            Homepage homepage = new OpenHomepageFlow().run();
            LoginPage loginPage = homepage.header.clickLoginLink();

            loginPage.fillLoginDetails();
            homepage = loginPage.clickLogin();

            ManagedAdsPage managedAdsPage = homepage.header.clickManagedAds();
            managedAdsPage.exportAllSmallAds(smallAdContainer);
            managedAdsPage.header.clickLogoutLink();
        }
        catch (RuntimeException | AssertionError e)
        {
            System.out.println("Failed to export small ads");
            System.out.println("Error was: " + e.toString());

            if (Context.get().getConfiguration().projectDebug())
            {
                e.printStackTrace();
            }

            return false;
        }

        return true;
    }

    public SmallAdContainer getSmallAdContainer()
    {
        return smallAdContainer;
    }
}
