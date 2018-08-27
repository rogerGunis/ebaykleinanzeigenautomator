package de.unik.ebaykleinanzeigenautomator;

import de.unik.ebaykleinanzeigenautomator.flows.OpenHomepageFlow;
import de.unik.ebaykleinanzeigenautomator.models.AdFilter;
import de.unik.ebaykleinanzeigenautomator.models.SmallAdContainer;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.Homepage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.LoginPage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.ManagedAdsPage;
import de.unik.ebaykleinanzeigenautomator.util.Context;

public class App 
{
    public static void main( String[] args )
    {
    		Context.initialize();
    		
        Homepage homepage = OpenHomepageFlow.run();
        LoginPage loginPage = homepage.header.clickLogin();
        
        loginPage.fillLoginDetails();
        homepage = loginPage.clickLogin();
        
        ManagedAdsPage managedAdsPage = homepage.header.clickManagedAds();
        
        SmallAdContainer smallAdContainer = new SmallAdContainer();
        managedAdsPage.pullAds(smallAdContainer, AdFilter.emptyFilter());
        
        homepage.header.clickLogout();

        System.out.println(smallAdContainer.toString());
    }
}
