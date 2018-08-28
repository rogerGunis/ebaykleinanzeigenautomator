package de.unik.ebaykleinanzeigenautomator.pageobjects.pages;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.SelenideElement;

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
	
	public void activateAllSmallAds()
	{
		processAndCollectAllSmallAds("Activating", null, s -> isInactive(s), s -> activateSmallAd(s));
	}
	
	public void deactivateAllSmallAds()
	{
		processAndCollectAllSmallAds("Dectivating", null, s -> isActive(s), s -> deactivateSmallAd(s));
	}
	
	public void deleteAllActiveSmallAds()
	{
		// For testing purposes delete inactive ads only
		processAndCollectAllSmallAds("Deleting", null, s -> isInactive(s), s -> deleteActiveSmallAd(s));
	}

	public void pullAllSmallAds(SmallAdContainer smallAdContainer)
	{
		processAndCollectAllSmallAds("Pulling", smallAdContainer, s -> { return true; }, s -> pullSmallAd(s));
	}
	
	private void processAndCollectAllSmallAds(String operation, SmallAdContainer smallAdContainer, Predicate<SelenideElement> predicate, Function<SelenideElement, SmallAd> function)
	{
		// Validate
		itemList.findAll("li.cardbox").shouldHave(CollectionCondition.sizeGreaterThan(0));

		int itemCount = itemList.findAll("li.cardbox").size();
		
		boolean applied = false;

		for(int i=0; i<itemCount; i++)
		{
			// Re-evaluate elements collection each iteration
			SelenideElement currentSmallAdElement = itemList.findAll("li.cardbox").get(i).should(exist);
			
			// Check if we need to apply our function to this element
			if(predicate.test(currentSmallAdElement))
			{
				// Print status
				System.out.println(operation + " " + currentSmallAdElement.find(".manageaditem-ad a").shouldBe(visible).text() + "' (" + (i + 1) + "/" + itemCount + ")");
	
				if(smallAdContainer != null)
				{
					// Execute function and collect
					smallAdContainer.smallAdds.add(function.apply(currentSmallAdElement));
				}
				else
				{
					// Execute only
					function.apply(currentSmallAdElement);
				}
				
				// Indicate that we could execute our operation at least once
				applied = true;
			}
		}
		
		if(!applied)
		{
			System.out.println("No applicable small ads found in account '" + Context.get().getAccount().username + "'");
		}
	}

	private boolean isInactive(SelenideElement smallAdElement)
	{
		$("a.managead-listitem-action-activate.is-hidden, a.managead-listitem-action-deactivate.is-hidden").should(exist);
		
		return smallAdElement.$("a.managead-listitem-action-deactivate.is-hidden").exists();
	}

	private SmallAd activateSmallAd(SelenideElement smallAdElement)
	{
		$("a.managead-listitem-action-activate.is-hidden, a.managead-listitem-action-deactivate.is-hidden").should(exist);
		
		smallAdElement.$("a.managead-listitem-action-activate").shouldBe(visible).scrollTo().click();

		return null;
	}

	private boolean isActive(SelenideElement smallAdElement)
	{
		$("a.managead-listitem-action-activate.is-hidden, a.managead-listitem-action-deactivate.is-hidden").should(exist);
		
		return smallAdElement.$("a.managead-listitem-action-activate.is-hidden").exists();
	}

	private SmallAd deactivateSmallAd(SelenideElement smallAdElement)
	{
		$("a.managead-listitem-action-activate.is-hidden, a.managead-listitem-action-deactivate.is-hidden").should(exist);
		
		smallAdElement.$("a.managead-listitem-action-deactivate").shouldBe(visible).scrollTo().click();
		
		return null;
	}
	
	private SmallAd deleteActiveSmallAd(SelenideElement smallAdElement)
	{
		// Click delete button
		smallAdElement.$("a.managead-listitem-action-delete").shouldBe(visible).scrollTo().click();
		
		// Confirm deletion in pop up
		$("#modal-bulk-delete-ad-sbmt").shouldBe(visible).scrollTo().click();
		
		// Close confirmation pop up
		$("#modal-bulk-delete-ad .mfp-close").shouldBe(visible).scrollTo().click();

		// Expect modal to be hidden
		$("#modal-bulk-delete-ad").shouldNotBe(visible);

		return null;
	}

	private SmallAd pullSmallAd(SelenideElement smallAdElement)
	{
		SmallAd smallAd = new SmallAd();

		// Get activation status
		smallAd.isActive = isActive(smallAdElement);

		// Get identifier
		smallAdElement.shouldHave(attribute("data-adid"));
		String id = smallAdElement.getAttribute("data-adid");
		Assert.assertTrue("Ad id must exist and not be empty.", StringUtils.isNotBlank(id));
		smallAd.id = id;

		// We need to activate inactive small ads first (otherwise we can not pull all details)
		boolean wasTemporarilyActivated = false;
		if(!smallAd.isActive)
		{
			activateSmallAd(smallAdElement);
			wasTemporarilyActivated = true;
		}

		// Retrieve further information from small ad detail page
		pullSmallAdDetails(smallAdElement, smallAd);
		
		// Deactivate if necessary
		if(wasTemporarilyActivated)
		{
			deactivateSmallAd(smallAdElement);
		}

		return smallAd;
	}
	
	private void pullSmallAdDetails(SelenideElement smallAdElement, SmallAd smallAd)
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
