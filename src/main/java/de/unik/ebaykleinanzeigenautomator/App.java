package de.unik.ebaykleinanzeigenautomator;

import de.unik.ebaykleinanzeigenautomator.flows.OpenHomepageFlow;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.Homepage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.LoginPage;

public class App 
{
    public static void main( String[] args )
    {
        Homepage homepage = OpenHomepageFlow.run();
        LoginPage loginPage = homepage.header.clickLogin();
        
        loginPage.fillLoginDetails();
        homepage = loginPage.clickLogin();
        
        homepage = homepage.header.logout();
    }
}
