# ebaykleinanzeigenautomator

Automates small ad (classifieds) export and import from and to [ebay-kleinanzeigen](https://www.ebay-kleinanzeigen.de).

Does not use the official ebay-kleinanzeigen classifieds API, but uses Selenide to browse the site and simulate user input. (The official API was not available at the time of writing this application.)

Provides a small console UI to execute the following options:

* Export all existing small ads from the site to your harddisk
* Activate all small ads at the site
* Deactivate all small ads at the site
* Delete inactive small ads at the site
* Delete active small ads at the site
* Import active small ads from your harddisk to the site
* Import active and not yet existing small ads from your harddisk to the site
* Import all small ads from your harddisk to the site

You have to provide your account credentials (will not be stored in any way). Stores small ad information in human-readable JSON format on your harddisk.

Account and small ad data are handled separately. Thus it is possible to import data from one account into another.

## Requirements

Requires that a browser and webdriver (chromedriver, geckodriver, ..) is installed. You can choose the type of browser in the project properties or via below commandline switch. 

## Commandline switches

You might wanna play with the following command line options:
```
-webdriver.chrome.driver=/path/to/chromedriver
-selenide.browser=chrome|firefox|..
-selenide.headless=true|false
-project.credentialsFromConfiguration=true|false
-account.username=..
-account.password=..
-project.dataDirectory=./data
-session=123456789
```
The session parameter will resume a previous session. The session identifier provided can be found in the JSON or the directory name under /data.

In case you want to start the application from within your Java IDE there are further options in config/project.properties, e.g. enabling debug output. By default the application is configured for console interaction and headless browser usage.

## Disclaimer

Use with reason. Mass uploading or duplicating small ads is violating ebay-kleinanzeigen terms and conditions (and you will be blocked by the site anyway). The application was written to backup and refresh your small ads every now and then, not to bloat the platform.