package de.unik.ebaykleinanzeigenautomator.flows;

import de.unik.ebaykleinanzeigenautomator.datamodels.SmallAdContainer;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.Homepage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.LoginPage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.ManagedAdsPage;

public class PullSmallAdContainerFlow
{
    public SmallAdContainer run()
    {
        Homepage homepage = new OpenHomepageFlow().run();
        LoginPage loginPage = homepage.header.clickLoginLink();

        loginPage.fillLoginDetails();
        homepage = loginPage.clickLogin();

        ManagedAdsPage managedAdsPage = homepage.header.clickManagedAds();

        SmallAdContainer smallAdContainer = new SmallAdContainer();
        managedAdsPage.pullAllSmallAds(smallAdContainer);

        managedAdsPage.header.clickLogoutLink();

        return smallAdContainer;
    }
}
