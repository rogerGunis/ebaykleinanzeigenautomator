package de.unik.ebaykleinanzeigenautomator.flows;

import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.Homepage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.LoginPage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.ManagedAdsPage;

public class DeleteAllActiveSmallAdsFlow
{
	public void run()
	{
        Homepage homepage = new OpenHomepageFlow().run();
        LoginPage loginPage = homepage.header.clickLogin();
        
        loginPage.fillLoginDetails();
        homepage = loginPage.clickLogin();
        
        ManagedAdsPage managedAdsPage = homepage.header.clickManagedAds();
        
    		managedAdsPage.deleteAllActiveSmallAds();
        
    		managedAdsPage.header.clickLogout();
	}
}
