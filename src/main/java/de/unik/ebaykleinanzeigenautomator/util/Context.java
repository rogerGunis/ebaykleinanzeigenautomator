package de.unik.ebaykleinanzeigenautomator.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.aeonbits.owner.ConfigFactory;

import de.unik.ebaykleinanzeigenautomator.datamodels.Account;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

public class Context
{
    private static Context context = null;

    private Configuration configuration = null;

    private String sessionIdentifier = null;

    private Account account = null;

    private Context()
    {
        configuration = ConfigFactory.create(Configuration.class, System.getProperties(), System.getenv());
    }

    public static void initialize()
    {
        get().initializeSelenide();
        get().account = new Account();

        if(!Context.get().getConfiguration().projectDebug())
        {
            // Deactivate output of java.util.logging 
            Logger logger = Logger.getLogger("");
            logger.setLevel(Level.OFF);
            logger.removeHandler(logger.getHandlers()[0]);
            logger.setUseParentHandlers(false);
            
            // Disable chromedriver output
            if(get().getConfiguration().selenideBrowser().toLowerCase().equals("chrome"))
            {
                System.setProperty("webdriver.chrome.silentOutput", "true");
            }
        }
        else
        {
            // Configure better logging output
            System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tT %4$s %5$s%6$s%n");
        }
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
        // disable selenium detection
        com.codeborne.selenide.Configuration.browserCapabilities = getDesiredCapabilitiesForChrome();

        // The browser
        com.codeborne.selenide.Configuration.browser = configuration.selenideBrowser();

        // Headless mode
        com.codeborne.selenide.Configuration.headless = false;

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
    }

    private DesiredCapabilities getDesiredCapabilitiesForChrome() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--window-size=1920,1080");

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        return capabilities;
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
        if (identifier == null)
        {
            sessionIdentifier = "" + System.currentTimeMillis();
        }
        else
        {
            sessionIdentifier = identifier;
        }
    }
    
    public boolean isValidSession()
    {
    	return sessionIdentifier != null;
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
        return getWorkingDirectoryPath(Context.get().getSessionIdentifier());
    }

    public String getWorkingDirectoryPath(String sessionIdentifier)
    {
        return Context.get().getConfiguration().projectDataDirectory() + "/" + sessionIdentifier + "/";
    }

    public String getWorkingFilePath()
    {
        return getWorkingFilePath(sessionIdentifier);
    }

    public String getWorkingFilePath(String sessionIdentifier)
    {
        return getWorkingDirectoryPath(sessionIdentifier) + Context.get().getConfiguration().projectDataFile();
    }
}
