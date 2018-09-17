package de.unik.ebaykleinanzeigenautomator.flows;

import java.util.Iterator;

import com.codeborne.selenide.Selenide;

import de.unik.ebaykleinanzeigenautomator.datamodels.SmallAd;
import de.unik.ebaykleinanzeigenautomator.datamodels.SmallAdContainer;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.EditAdDetailsPage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.Homepage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.LoginPage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.ManagedAdsPage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.PostAdConfirmPage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.PostAdSelectCategoryPage;
import de.unik.ebaykleinanzeigenautomator.util.Context;

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
            boolean imported = false;

            while (smallAdsIterator.hasNext())
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
                
                // Post small ad
                postAdSelectCategoryPage.selectCategories(smallAd);

                EditAdDetailsPage editAdDetailsPage = postAdSelectCategoryPage.clickNext();
                PostAdConfirmPage postAdConfirmPage = editAdDetailsPage.publishAd(smallAd);

                System.out.println("Imported " + smallAd.title);

                // With each posted ad wait a little to minimize account lock risk due to automation
                Selenide.sleep(Context.get().getConfiguration().projectAdImportDelay());

                homepage = postAdConfirmPage.header.clickHome();

                imported = true;
            }

            homepage.header.clickLogoutLink();

            if (!imported)
            {
                System.out.println("No applicable small ads found for account " + Context.get().getAccount().username);
            }
        }
        catch (Throwable t)
        {
            System.out.println("Failed to import small ads");
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
