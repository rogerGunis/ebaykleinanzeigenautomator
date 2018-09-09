package de.unik.ebaykleinanzeigenautomator.util;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;

@LoadPolicy(LoadType.MERGE)
@Sources({ "file:config/project.properties", "file:config/credentials.properties" })
public interface Configuration extends Config
{
    @DefaultValue("ebay-kleinanzeigen.de")
    @Key("site.domain")
    public String siteDomain();

    @DefaultValue("https://www.ebay-kleinanzeigen.de")
    @Key("site.url")
    public String siteUrl();

    @Key("account.username")
    public String accountUsername();

    @Key("account.password")
    public String accountPassword();

    @DefaultValue("Chrome")
    @Key("selenide.browser")
    public String selenideBrowser();

    @DefaultValue("true")
    @Key("selenide.headless")
    public boolean selenideHeadless();

    @DefaultValue("false")
    @Key("selenide.holdBrowserOpen")
    public boolean selenideHoldBrowserOpen();

    @DefaultValue("true")
    @Key("selenide.startMaximized")
    public boolean selenideStartMaximized();

    @DefaultValue("false")
    @Key("selenide.recordScreenshots")
    public boolean selenideRecordScreenshots();

    @DefaultValue("./reports")
    @Key("selenide.reportsFolder")
    public String selenideReportsFolder();

    @DefaultValue("normal")
    @Key("selenide.pageLoadStrategy")
    public String selenidePageLoadStrategy();

    @DefaultValue("false")
    @Key("selenide.savePageSource")
    public boolean selenideSavePageSource();

    @DefaultValue("8000")
    @Key("selenide.timeout")
    public long selenideTimeout();

    @DefaultValue("10000")
    @Key("selenide.fileDownloadTimeout")
    public long selenideFileDownloadTimeout();

    @DefaultValue("false")
    @Key("project.debug")
    public boolean projectDebug();

    @DefaultValue("./data")
    @Key("project.dataDirectory")
    public String projectDataDirectory();

    @DefaultValue("small-ads.json")
    @Key("project.dataFile")
    public String projectDataFile();

    @DefaultValue("true")
    @Key("project.downloadImages")
    public boolean projectDownloadImages();
    
    @DefaultValue("false")
    @Key("project.credentialsFromConfiguration")
    public boolean credentialsFromConfiguration();

    @DefaultValue("true")
    @Key("project.systemConsoleInput")
    public boolean systemConsoleInput();
    
    @DefaultValue("7500")
    @Key("project.generalDelay")
    public long projectGeneralDelay();

    @DefaultValue("10000")
    @Key("project.adImportDelay")
    public long projectAdImportDelay();
}