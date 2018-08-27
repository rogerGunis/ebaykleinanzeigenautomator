package de.unik.ebaykleinanzeigenautomator.flows;

import de.unik.ebaykleinanzeigenautomator.models.AdFilter;
import de.unik.ebaykleinanzeigenautomator.models.SmallAdContainer;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.Homepage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.LoginPage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.ManagedAdsPage;

public class PullSmallAdContainerFlow
{
	private PullSmallAdContainerFlow()
	{
	}
	
	public static void run()
	{
        Homepage homepage = OpenHomepageFlow.run();
        LoginPage loginPage = homepage.header.clickLogin();
        
        loginPage.fillLoginDetails();
        homepage = loginPage.clickLogin();
        
        ManagedAdsPage managedAdsPage = homepage.header.clickManagedAds();
        
        SmallAdContainer smallAdContainer = new SmallAdContainer();
        managedAdsPage.pullAds(smallAdContainer, AdFilter.emptyFilter());
        
        homepage.header.clickLogout();

        smallAdContainer.writeToDisk();
	}
}
