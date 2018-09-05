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
        if (smallAdContainer == null)
        {
            System.out.println("Small ad container is not initialized");
            return false;
        }

        try
        {
            Homepage homepage = new OpenHomepageFlow().run();
            LoginPage loginPage = homepage.header.clickLoginLink();

            loginPage.fillLoginDetails();
            homepage = loginPage.clickLogin();

            ManagedAdsPage managedAdsPage = homepage.header.clickManagedAds();
            managedAdsPage.exportAllSmallAds(smallAdContainer);
            managedAdsPage.header.clickLogoutLink();
        }
        catch (Throwable t)
        {
            System.out.println("Failed to export small ads");
            System.out.println("Error was: " + t.getMessage());

            if (Context.get().getConfiguration().projectDebug())
            {
                t.printStackTrace();
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
