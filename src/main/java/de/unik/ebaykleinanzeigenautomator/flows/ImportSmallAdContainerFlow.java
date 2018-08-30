package de.unik.ebaykleinanzeigenautomator.flows;

import java.util.Iterator;

import de.unik.ebaykleinanzeigenautomator.datamodels.SmallAd;
import de.unik.ebaykleinanzeigenautomator.datamodels.SmallAdContainer;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.EditAdDetailsPage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.Homepage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.LoginPage;
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

    public boolean run()
    {
        try
        {
            Homepage homepage = new OpenHomepageFlow().run();
            LoginPage loginPage = homepage.header.clickLoginLink();
    
            loginPage.fillLoginDetails();
            homepage = loginPage.clickLogin();

            // Loop through small ad container
            Iterator<SmallAd> smallAdIterator = smallAdContainer.smallAds.iterator();
            while(smallAdIterator.hasNext())
            {
                SmallAd smallAd = smallAdIterator.next();
                if(smallAd.isActive)
                {
                    System.out.println("Importing '" + smallAd.title + "'");
                    
                    // Post active small ads
                    PostAdSelectCategoryPage postAdSelectCategoryPage = homepage.header.clickPostAd();
                    postAdSelectCategoryPage.selectCategories(smallAd);
                    
                    EditAdDetailsPage editAdDetailsPage = postAdSelectCategoryPage.clickNext();  
                    PostAdConfirmPage postAdConfirmPage = editAdDetailsPage.publishAd(smallAd);
                    
                    homepage = postAdConfirmPage.header.clickHome();
                }
            }
    
            homepage.header.clickLogoutLink();
        }
        catch(Throwable t)
        {
            System.out.println("Failed to import small ad container");
            System.out.println("Error was: " + t.getMessage());
            
            if(Context.get().getConfiguration().projectDebug())
            {
                t.printStackTrace();
            }
            
            return false;
        }

        return true;
    }
}
