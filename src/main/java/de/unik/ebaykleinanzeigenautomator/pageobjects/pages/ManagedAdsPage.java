package de.unik.ebaykleinanzeigenautomator.pageobjects.pages;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import de.unik.ebaykleinanzeigenautomator.datamodels.SmallAd;
import de.unik.ebaykleinanzeigenautomator.datamodels.SmallAdContainer;
import de.unik.ebaykleinanzeigenautomator.pageobjects.components.Pagination;
import de.unik.ebaykleinanzeigenautomator.util.Context;

public class ManagedAdsPage extends BrowsingPage
{
    private static final String SMALL_AD_ITEM_LOCATOR = "li.cardbox";
    
    public Pagination pagination = new Pagination();

    private SelenideElement managedAdsContent = $("#my-manageads-content");

    private SelenideElement itemList = managedAdsContent.$(".itemlist");
    
    private int itemCountPerPage;
    private int itemCounter;


    @Override
    public void validateIsExpectedPage()
    {
        super.validateIsExpectedPage();

        $("#my-manageads").should(exist);
    }
    
    public void activateAllSmallAds()
    {
        processSmallAds("Activated", null, s -> isInactive(s), s -> activateSmallAd(s), false);
    }

    public void deactivateAllSmallAds()
    {
        processSmallAds("Deactivated", null, s -> isActive(s), s -> deactivateSmallAd(s), false);
    }

    public void deleteInactiveSmallAds()
    {
        processSmallAds("Deleted", null, s -> isInactive(s), s -> deleteSmallAd(s), true);
    }

    public void deleteActiveSmallAds()
    {
        processSmallAds("Deleted", null, s -> isActive(s), s -> deleteSmallAd(s), true);
    }

    public void exportAllSmallAds(SmallAdContainer smallAdContainer)
    {
        processSmallAds("Exported", smallAdContainer, s -> { return true; }, s -> exportSmallAd(s), false);
    }
    
    public boolean smallAdExists(SmallAd smallAd)
    {
        resetItemCounter();

        while (moreItemsAvailable())
        {
            // Re-evaluate elements collection each iteration
            SelenideElement currentSmallAdElement = getCurrentItemFromPage().scrollTo();
            
            // Get title
            String title = currentSmallAdElement.find(".manageaditem-main .manageaditem-ad > h2 > a").shouldBe(visible).text();

            // Check if we this matches the given small ad in title 
            if (smallAd.title.equals(title))
            {
                return true;
            }
            
            // Advance
            itemCounter++;
        }
        
        return false;
    }

    private void processSmallAds(String operation, SmallAdContainer smallAdContainer, Predicate<SelenideElement> predicate, Function<SelenideElement, SmallAd> function, boolean modifiesItemList)
    {
        boolean applied = false;

        resetItemCounter();

        while (moreItemsAvailable())
        {
            // Re-evaluate elements collection each iteration
            SelenideElement currentSmallAdElement = getCurrentItemFromPage().scrollTo();

            // Check if we need to apply our function to this element
            if (predicate.test(currentSmallAdElement))
            {
                if (smallAdContainer != null)
                {
                    // Execute function and collect
                    smallAdContainer.smallAds.add(function.apply(currentSmallAdElement));
                }
                else
                {
                    // Execute only
                    function.apply(currentSmallAdElement);
                }

                // Get title and print status
                String title = currentSmallAdElement.find(".manageaditem-main .manageaditem-ad > h2 > a").shouldBe(visible).text();
                System.out.println(operation + " " + title);

                // If the operation modified our item list at the page
                if (modifiesItemList)
                {
                    // We now have fewer items
                    itemCountPerPage--;
                    itemCounter--;

                    // Validate that list changed
                    itemList.findAll(SMALL_AD_ITEM_LOCATOR).shouldHaveSize(itemCountPerPage);
                }
                
                // Indicate that we could execute our operation at least once
                applied = true;
            }
            
            // Advance
            itemCounter++;
        }

        if (!applied)
        {
            System.out.println("No applicable small ads found in account " + Context.get().getAccount().username);
        }
    }
    
    private boolean moreItemsAvailable()
    {
        if(itemCounter == itemCountPerPage)
        {
            // Handle pagination
            if(pagination.isPossible())
            {
                pagination.apply();

                // Retrieve new item list information and reset counter
                resetItemCounter();
            }
        }
        
        return itemCounter < itemCountPerPage;
    }
    
    private void resetItemCounter()
    {
        itemCountPerPage = itemList.should(exist).findAll(SMALL_AD_ITEM_LOCATOR).size();
        itemCounter = 0;
    }
    
    private SelenideElement getCurrentItemFromPage()
    {
        return itemList.findAll(SMALL_AD_ITEM_LOCATOR).get(itemCounter).should(exist);
    }

    private boolean isInactive(SelenideElement smallAdElement)
    {
        smallAdElement.$("a.managead-listitem-action-activate.is-hidden, a.managead-listitem-action-deactivate.is-hidden").should(exist).scrollTo();

        return smallAdElement.$("a.managead-listitem-action-activate").is(visible);
    }

    private SmallAd activateSmallAd(SelenideElement smallAdElement)
    {
        smallAdElement.$("a.managead-listitem-action-activate").should(exist).scrollTo().shouldBe(visible).click();

        return null;
    }

    private boolean isActive(SelenideElement smallAdElement)
    {
        smallAdElement.$("a.managead-listitem-action-activate.is-hidden, a.managead-listitem-action-deactivate.is-hidden").should(exist).scrollTo();

        return smallAdElement.$("a.managead-listitem-action-deactivate").is(visible);
    }

    private SmallAd deactivateSmallAd(SelenideElement smallAdElement)
    {
        smallAdElement.$("a.managead-listitem-action-deactivate").should(exist).scrollTo().shouldBe(visible).click();

        return null;
    }

    private SmallAd deleteSmallAd(SelenideElement smallAdElement)
    {
        // Click delete button
        smallAdElement.$("a.managead-listitem-action-delete").should(exist).scrollTo().shouldBe(visible).click();

        // Confirm deletion in pop up
        $("#modal-bulk-delete-ad-sbmt").should(exist).scrollTo().shouldBe(visible).click();

        // Close confirmation pop up
        $("#modal-bulk-delete-ad .mfp-close").should(exist).scrollTo().shouldBe(visible).click();

        // Expect modal to be hidden
        $("#modal-bulk-delete-ad").shouldNotBe(visible);

        return null;
    }

    private SmallAd exportSmallAd(SelenideElement smallAdElement)
    {
        SmallAd smallAd = new SmallAd();

        // Lets see whats going on
        smallAdElement.scrollTo();

        // Get activation status
        smallAd.isActive = isActive(smallAdElement);

        // Get identifier
        smallAdElement.shouldHave(attribute("data-adid"));
        smallAd.id = smallAdElement.getAttribute("data-adid");
        if(StringUtils.isBlank(smallAd.id))
        {
            // In the unlikely case that the page does have an error, because we need the id for our image file names
            smallAd.id = UUID.randomUUID().toString();
        }

        // We need to activate inactive small ads first (otherwise we can not export all details)
        boolean wasTemporarilyActivated = false;
        if (!smallAd.isActive)
        {
            activateSmallAd(smallAdElement);
            wasTemporarilyActivated = true;
        }

        // Retrieve further information from small ad detail page
        exportSmallAdDetails(smallAdElement, smallAd);

        // Deactivate if necessary
        if (wasTemporarilyActivated)
        {
            deactivateSmallAd(smallAdElement);
        }

        return smallAd;
    }

    private void exportSmallAdDetails(SelenideElement smallAdElement, SmallAd smallAd)
    {
        // Open ad detail page
        smallAdElement.$("section.manageaditem-ad > h2 > a").should(exist).scrollTo().shouldBe(visible).click();

        // Create small ad detail page and export details
        AdDetailsPage adDetailPage = new AdDetailsPage();
        adDetailPage.exportAdDetails(smallAd);

        // Go back to small ad overview (keeps pagination in tact)
        Selenide.back();
    }
}
