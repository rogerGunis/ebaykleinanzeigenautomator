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
        catch (RuntimeException | AssertionError e)
        {
            System.out.println("Failed to login. Please provide valid account credentials.");
            System.out.println("Error was: " + e.toString());

            if (Context.get().getConfiguration().projectDebug())
            {
                e.printStackTrace();
            }

            return false;
        }

        return true;
    }
}
