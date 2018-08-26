package de.unik.ebaykleinanzeigenautomator.util;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;

@LoadPolicy(LoadType.MERGE)
@Sources({ "file:project.properties" })
public interface Configuration extends Config
{
	@Key("site.domain")
	public String siteDomain();
	
	@Key("site.url")
	public String siteUrl();

	@Key("account.username")
	public String accountUsername();

	@Key("account.password")
	public String accountPassword();

	@Key("selenide.browser")
	public String selenideBrowser();

	@Key("selenide.headless")
	public boolean selenideHeadless();

	@Key("selenide.holdBrowserOpen")
	public boolean selenideHoldBrowserOpen();

	@Key("selenide.startMaximized")
	public boolean selenideStartMaximized();
	
	@Key("selenide.recordScreenshots")
	public boolean selenideRecordScreenshots();

	@Key("selenide.reportsFolder")
	public String selenideReportsFolder();

	@Key("selenide.pageLoadStrategy")
	public String selenidePageLoadStrategy();

	@Key("selenide.savePageSource")
	public boolean selenideSavePageSource();
	
	@Key("selenide.timeout")
	public long selenideTimeout();
}