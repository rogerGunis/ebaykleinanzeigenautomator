package de.unik.ebaykleinanzeigenautomator.pageobjects.pages;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.SelenideElement;

import de.unik.ebaykleinanzeigenautomator.datamodels.AdFilter;
import de.unik.ebaykleinanzeigenautomator.datamodels.SmallAd;
import de.unik.ebaykleinanzeigenautomator.datamodels.SmallAdContainer;
import de.unik.ebaykleinanzeigenautomator.util.Context;


public class ManagedAdsPage extends BrowsingPage
{
	private SelenideElement managedAdsContent = $("#my-manageads-content");
	
	private SelenideElement itemList = managedAdsContent.$(".itemlist");
	
	@Override
	public void validateIsExpectedPage()
	{
		super.validateIsExpectedPage();
		
		$("#my-manageads").should(exist);
	}
	
	public void pullAds(SmallAdContainer smallAdContainer, AdFilter adFilter)
	{
		// Validate
		itemList.findAll("li.cardbox").shouldHave(CollectionCondition.sizeGreaterThan(0));
		
		int itemCount = itemList.findAll("li.cardbox").size();

		for(int i=0; i<itemCount; i++)
		{
			// Current small ad data object
			SmallAd smallAd = new SmallAd();
			
			// Re-evaluate elements collection each iteration
			SelenideElement currentSmallAd = itemList.findAll("li.cardbox").get(i).should(exist);
			
			// Print status
			System.out.println("Pulling details of small ad '" + currentSmallAd.find(".manageaditem-ad a").shouldBe(visible).text() + "' (" + (i + 1) + "/" + itemCount + ")");

			// Pull overview
			pullSmallAdOverview(smallAd, currentSmallAd);
			
			// Pull details by opening detail page
			pullSmallAdDetails(smallAd, currentSmallAd);
			
			// Collect
			smallAdContainer.smallAdds.add(smallAd);
		}
		
		if(itemCount == 0)
		{
			System.out.println("No small ads found for account '" + Context.get().getAccount().username + "'");
		}
	}
	
	private void pullSmallAdOverview(SmallAd smallAd, SelenideElement smallAdElement)
	{
		// Id
		smallAdElement.shouldHave(attribute("data-adid"));
		String id = smallAdElement.getAttribute("data-adid");
		Assert.assertTrue("Ad id must exist and not be empty.", StringUtils.isNotBlank(id));
		smallAd.id = id;

		// Activation status
		$("a.managead-listitem-action-activate.is-hidden, a.managead-listitem-action-deactivate.is-hidden").should(exist);
		smallAd.isActive = smallAdElement.$("a.managead-listitem-action-activate.is-hidden").exists() ? true : false;
	}
	
	private void pullSmallAdDetails(SmallAd smallAd, SelenideElement smallAdElement)
	{
		// Open ad detail page
		smallAdElement.$("section.manageaditem-ad > h2 > a").shouldBe(visible).click();
		
		// Create small ad detail page and pull details
		AdDetailPage adDetailPage = new AdDetailPage();
		adDetailPage.pullAdDetails(smallAd);
		
		// Leave small ad details page
		adDetailPage.header.clickManagedAds();
	}
}
