package de.unik.ebaykleinanzeigenautomator;

import com.codeborne.selenide.Selenide;

import de.unik.ebaykleinanzeigenautomator.flows.OpenHomepageFlow;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.Homepage;
import de.unik.ebaykleinanzeigenautomator.pageobjects.pages.LoginPage;
import de.unik.ebaykleinanzeigenautomator.util.Context;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println("Site url: " + Context.get().getConfiguration().siteUrl());
				
        Homepage homepage = OpenHomepageFlow.run();
        LoginPage loginPage = homepage.header.clickLogin();
        
        loginPage.fillLoginDetails();
        homepage = loginPage.clickLogin();
        
        Selenide.sleep(3000);
        
        homepage = homepage.header.logout();
    }
}
