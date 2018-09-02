package de.unik.ebaykleinanzeigenautomator.util;

import org.aeonbits.owner.ConfigFactory;

import de.unik.ebaykleinanzeigenautomator.datamodels.Account;

public class Context
{
    private static Context context = null;

    private Configuration configuration = null;

    private String sessionIdentifier = null;

    private Account account = null;

    private Context()
    {
        configuration = ConfigFactory.create(Configuration.class, System.getProperties(), System.getenv());

        resetSessionIdentifier(null);
    }

    public static void initialize()
    {
        get().initializeSelenide();
        get().account = new Account();
    }

    public static Context get()
    {
        if (context == null)
        {
            context = new Context();
        }

        return context;
    }

    private void initializeSelenide()
    {
        // The browser
        com.codeborne.selenide.Configuration.browser = configuration.selenideBrowser();

        // Headless mode
        com.codeborne.selenide.Configuration.headless = configuration.selenideHeadless();

        // Hold browser open
        com.codeborne.selenide.Configuration.holdBrowserOpen = configuration.selenideHoldBrowserOpen();

        // Start browser maximized
        com.codeborne.selenide.Configuration.startMaximized = configuration.selenideStartMaximized();

        // Take screenshots if a test fails
        com.codeborne.selenide.Configuration.screenshots = configuration.selenideRecordScreenshots();

        // Location of the directory where the screenshots/reports from Selenide will be saved
        com.codeborne.selenide.Configuration.reportsFolder = configuration.selenideReportsFolder() + "/" + sessionIdentifier;

        // Process the page after the load event fires
        // - normal -> return after load event
        // - eager -> return after DOMContentLoaded
        // - none -> return immediately
        com.codeborne.selenide.Configuration.pageLoadStrategy = configuration.selenidePageLoadStrategy();

        // In case of an error please save the page source
        com.codeborne.selenide.Configuration.savePageSource = configuration.selenideSavePageSource();

        // Tests will fail after x milliseconds if the given condition does not match
        com.codeborne.selenide.Configuration.timeout = configuration.selenideTimeout();

        // Timeout for collection look ups
        com.codeborne.selenide.Configuration.collectionsTimeout = configuration.selenideTimeout() * 2;
    }

    public Configuration getConfiguration()
    {
        return configuration;
    }

    public String getSessionIdentifier()
    {
        return sessionIdentifier;
    }

    public void resetSessionIdentifier(String identifier)
    {
        if(identifier == null)
        {
            sessionIdentifier = "" + System.currentTimeMillis();
        }
        else
        {
            sessionIdentifier = identifier;
        }
    }

    public void setAccount(String username, String password)
    {
        account.username = username;
        account.password = password;
    }

    public Account getAccount()
    {
        return account;
    }

    public String getWorkingDirectoryPath()
    {
       return Context.get().getConfiguration().projectDataDirectory() + "/" + Context.get().getSessionIdentifier() + "/";
    }

    public String getWorkingFilePath()
    {
       return getWorkingDirectoryPath() + Context.get().getConfiguration().projectDataFile();
    }
}
