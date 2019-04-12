package de.unik.ebaykleinanzeigenautomator.flows;

import java.util.Iterator;

import de.unik.ebaykleinanzeigenautomator.datamodels.SmallAd;
import de.unik.ebaykleinanzeigenautomator.datamodels.SmallAdContainer;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.EditAdDetailsPage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.Homepage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.LoginPage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.ManagedAdsPage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.PostAdConfirmPage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.PostAdSelectCategoryPage;
import de.unik.ebaykleinanzeigenautomator.util.Context;
import de.unik.ebaykleinanzeigenautomator.util.Util;

public class ImportSmallAdContainerFlow
{
    private SmallAdContainer smallAdContainer = null;

    public ImportSmallAdContainerFlow(SmallAdContainer smallAdContainer)
    {
        this.smallAdContainer = smallAdContainer;
    }

    public boolean run(boolean checkForActive, boolean checkForExists)
    {
        try
        {
            Homepage homepage = new OpenHomepageFlow().run();
            LoginPage loginPage = homepage.header.clickLoginLink();

            loginPage.fillLoginDetails();
            homepage = loginPage.clickLogin();

            // Loop through small ad container
            Iterator<SmallAd> smallAdsIterator = smallAdContainer.smallAds.iterator();
            
            // Loop states
            boolean importedAtLeastOnce = false;
            boolean errorOccurredWhilePublishing = false;
            boolean publishingCurrentlyBlocked = false;

            while (smallAdsIterator.hasNext() && !publishingCurrentlyBlocked)
            {
                SmallAd smallAd = smallAdsIterator.next();
                
                if (checkForActive && !smallAd.isActive)
                {
                    continue;
                }
                
                PostAdSelectCategoryPage postAdSelectCategoryPage;
                if(checkForExists)
                {
                    ManagedAdsPage managedAdsPage = homepage.header.clickManagedAds();
                    if(managedAdsPage.smallAdExists(smallAd))
                    {
                        // Small ad is already there, so go to next one in container
                        continue;
                    }
                    
                    // Does not exist yet, so go to post ad page
                    postAdSelectCategoryPage = managedAdsPage.header.clickPostAd();
                }
                else
                {
                    // No check required, simply go to post ad page
                    postAdSelectCategoryPage = homepage.header.clickPostAd();
                }
                
                // Browse and select appropriate small ad categories
                postAdSelectCategoryPage.selectCategories(smallAd);

                // Go to small ad details page
                EditAdDetailsPage editAdDetailsPage = postAdSelectCategoryPage.clickNext();
                
                // Check if ad publishing is currently blocked, e.g. small ad quota exceeded
                if(editAdDetailsPage.hasPublishBlocker())
                {
                	System.out.println("Publishing ad '" + smallAd.title + "' is currently not possible.");
                	System.out.println("Ebay Kleinanzeigen reported the following problem: " + editAdDetailsPage.getPublishBlockerMessage() + "\n");
                	errorOccurredWhilePublishing = true;
                	continue;
                }
                
                // Check for captcha
                if(editAdDetailsPage.hasCaptcha())
                {
                	System.out.println("Publishing ad '" + smallAd.title + "' is currently not possible.");
                	System.out.println("Ebay Kleinanzeigen detected that we are publishing ads in an automated fashion and requires user to solve a Captcha. Please try again later.\n");
                	errorOccurredWhilePublishing = true;
                	publishingCurrentlyBlocked = true;
                	continue;
                }
                
                // Everything seems to be okay, publish ad
                PostAdConfirmPage postAdConfirmPage = editAdDetailsPage.publishAd(smallAd);

                System.out.println("Imported " + smallAd.title);

                // With each posted ad wait a little to minimize publish block risk due to automation
                Util.waitOnPageLoad(Context.get().getConfiguration().projectAdImportDelay());

                homepage = postAdConfirmPage.header.clickHome();

                importedAtLeastOnce = true;
            }

            homepage.header.clickLogoutLink();

            if (!importedAtLeastOnce && !errorOccurredWhilePublishing)
            {
                System.out.println("No applicable small ads found for account " + Context.get().getAccount().username);
            }
        }
        catch (Throwable t)
        {
            System.out.println("Failed to import small ads");
            System.out.println("Error was: " + t.toString());

            if (Context.get().getConfiguration().projectDebug())
            {
                t.printStackTrace();
            }

            return false;
        }

        return true;
    }
}
