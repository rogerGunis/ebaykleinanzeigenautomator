package de.unik.ebaykleinanzeigenautomator.flows;

import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.Homepage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.LoginPage;
import de.unik.ebaykleinanzeigenautomator.util.Context;

public class LoginLogoutFlow
{
    public boolean run()
    {
        try
        {
            Homepage homepage = new OpenHomepageFlow().run();
            LoginPage loginPage = homepage.header.clickLoginLink();
    
            loginPage.fillLoginDetails();
            homepage = loginPage.clickLogin();
            
            homepage.header.clickLogoutLink();
        }
        catch (Throwable t)
        {
            System.out.println("Failed to login and logout. Wrong credentials?");
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
