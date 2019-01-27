# ebaykleinanzeigenautomator

Automates small ad (classifieds) export and import from and to [ebay-kleinanzeigen](https://www.ebay-kleinanzeigen.de).

Does not use the official ebay-kleinanzeigen classifieds API, but uses Selenide to browse the site and simulate user input. (The official API was not available at the time of writing this application.)

## Requirements

Requires a webbrowser, webdriver (chromedriver, geckodriver etc.) and Java. By default the application uses Chrome. You can choose the type of browser in the project properties or via commandline switch. Expects the webdriver to be available via your path variable, e.g. in `/user/bin/chromedriver`, but you can specify whatever location (see section commandline switches).

## Build and run

Build with Maven via `mvn package` with outputs located in `/target`.

Copy the outputs `ebaykleinanzeigenautomator.jar` and `/lib` to whatever location you need it to be.

Run from that location via `java -jar ebaykleinanzeigenautomator.jar`.

Run with commandline options and property overrides via `java -jar -D.. ebaykleinanzeigenautomator.jar -session=..` (see section commandline switches).

## Usage

Provides a small console UI to execute the following operations:

* Export all existing small ads from the site to your harddisk
* Activate all small ads at the site
* Deactivate all small ads at the site
* Delete inactive small ads at the site
* Delete active small ads at the site
* Import active small ads from your harddisk to the site
* Import active and not yet existing small ads from your harddisk to the site
* Import all small ads from your harddisk to the site

Typical use case would be to refresh ('push up') your existing small ads via the following operations:

1. Export (download) all existing small ads to your harddisk
2. Delete all active small ads at the site
3. Import (upload) all active small ads from hardisk to the site

When starting the application you have to provide your ebay-kleinanzeigen account credentials. The application will verify the provided credentials by logging in and out. The account will not be stored in any way on your harddisk. During export, small ad information (including images) is stored in human-readable JSON format on your harddisk (see `/data` directory).

Account and small ad data are handled separately. Thus it is possible to import data from one account into another. To move all you small ads from account A to account B use the following operations:

1. Login to account A at start of application
2. Export (download) all existing small ads to your harddisk
3. Set account credentials for account B
4. Import (upload) all active small ads from hardisk to the site

## Commandline switches

You might want to play with the following commandline options and property overrides (-D):
```
-session=123456789
-Dwebdriver.chrome.driver=/path/to/chromedriver
-Dselenide.browser=chrome|firefox|..
-Dselenide.headless=true|false
-Dproject.credentialsFromConfiguration=true|false
-Daccount.username=..
-Daccount.password=..
-Dproject.dataDirectory=./data
```
By default the application is configured for console interaction and Chrome browser in headless mode and will start from a clean slate (i.e. no existing data is loaded). See `config/project.properties` for all properties.

Each export operation stores session data in the `/data` directory. This directory contains all small ad data including small ad images (basically a backup of all your small ads in JSON format). You can 'resume' a previous session (load your backup) by providing the session identifier to the application via the `-session` commandline parameter. This will use the stored data, so you won't have to export all data from the site again. Since you can mix multiple accounts and data versions, each 'data session' represents a version of your small ad contents. The session identifier can be found in `small-ads.json` or the respective directory name under `/data/`.

In case you want to start the application from within your Java IDE there are further options in `config/project.properties`, e.g. enabling debug output. If you prefer, you can store your account credentials in ``config/credentials.properties`` and enable reading your credentials from this file via option `project.credentialsFromConfiguration` in `config/project.properties`. (A template file `credentials.properties.template` is given.) This comes in handy when using the application from with your Java IDE.

## Disclaimer

Use with reason. Mass uploading or duplicating small ads is violating ebay-kleinanzeigen terms and conditions (and will get you blocked by the site). The application was written to backup and refresh *your* small ads every now and then (which is cumbersome to do manually), not to spam the small ads platform.
