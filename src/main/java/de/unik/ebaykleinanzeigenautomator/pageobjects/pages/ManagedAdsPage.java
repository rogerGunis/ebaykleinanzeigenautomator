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
    
    private int totalItemPerPageCount;

    private int itemPerPageCounter;
    
    private int itemCounter;

    @Override
    public void validateIsExpectedPage()
    {
        super.validateIsExpectedPage();

        $("#my-manageads").should(exist);
    }
    
    public void queryAllSmallAds()
    {
    	processSmallAds(null, s -> true, s -> querySmallAdDetails(s), false);
    }
    
    public void activateAllSmallAds()
    {
        processSmallAds(null, s -> isInactive(s), s -> activateSmallAd(s), false);
    }

    public void deactivateAllSmallAds()
    {
        processSmallAds(null, s -> isActive(s), s -> deactivateSmallAd(s), false);
    }

    public void deleteInactiveSmallAds()
    {
        processSmallAds(null, s -> isInactive(s), s -> deleteSmallAd(s), true);
    }

    public void deleteActiveSmallAds()
    {
        processSmallAds(null, s -> isActive(s), s -> deleteSmallAd(s), true);
    }

    public void exportAllSmallAds(SmallAdContainer smallAdContainer)
    {
        processSmallAds(smallAdContainer, s -> { return true; }, s -> exportSmallAd(s), false);
    }
    
    public boolean smallAdExists(SmallAd smallAd)
    {
        resetItemPerPageCounter();

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
            itemPerPageCounter++;
        }
        
        return false;
    }
    
    private void processSmallAds(SmallAdContainer smallAdContainer, Predicate<SelenideElement> predicate, Function<SelenideElement, SmallAd> function, boolean modifiesItemList)
    {
        boolean applied = false;

        resetItemPerPageCounter();
        itemCounter = 0;

        while (moreItemsAvailable())
        {
            // Remember if there are multiple pages with items
            boolean multiPageItemList = pagination.isPossible();
            
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

                // If our operation modifies the item list, we need to adjust the counters
                if (modifiesItemList)
                {
                    // The modification only decreased out item list size if this was the only page with items
                    if(!multiPageItemList)
                    {
                        totalItemPerPageCount--;
                    }
                    
                    itemPerPageCounter--;

                    // Validate that our item list has the proper item count
                    itemList.findAll(SMALL_AD_ITEM_LOCATOR).shouldHaveSize(totalItemPerPageCount);
                }
                
                // Indicate that we could execute our operation at least once
                applied = true;
            }
            
            // Advance
            itemPerPageCounter++;
            itemCounter++;
        }

        if (!applied)
        {
            System.out.println("No applicable small ads found in account " + Context.get().getAccount().username);
        }
    }
    
    private boolean moreItemsAvailable()
    {
        if(itemPerPageCounter == totalItemPerPageCount)
        {
            // Handle pagination
            if(pagination.isPossible())
            {
                pagination.apply();

                // Retrieve new item list information and reset counter
                resetItemPerPageCounter();
            }
        }
        
        return itemPerPageCounter < totalItemPerPageCount;
    }
    
    private void resetItemPerPageCounter()
    {
        totalItemPerPageCount = itemList.should(exist).findAll(SMALL_AD_ITEM_LOCATOR).size();
        itemPerPageCounter = 0;
    }
    
    private SelenideElement getCurrentItemFromPage()
    {
        return itemList.findAll(SMALL_AD_ITEM_LOCATOR).get(itemPerPageCounter).should(exist);
    }

    private boolean isActive(SelenideElement smallAdElement)
    {
        smallAdElement.$("a.managead-listitem-action-activate.is-hidden, a.managead-listitem-action-deactivate.is-hidden").should(exist).scrollTo();

        return smallAdElement.$("a.managead-listitem-action-deactivate").is(visible);
    }

    private boolean isInactive(SelenideElement smallAdElement)
    {
        smallAdElement.$("a.managead-listitem-action-activate.is-hidden, a.managead-listitem-action-deactivate.is-hidden").should(exist).scrollTo();

        return smallAdElement.$("a.managead-listitem-action-activate").is(visible);
    }
    
    private void printElementOperationStatus(SelenideElement smallAdElement, String operation, String status)
    {
        // Get title
        String title = smallAdElement.find(".manageaditem-main .manageaditem-ad > h2 > a").should(exist).text();
        
        // Print status
        System.out.println(operation + (operation.isEmpty() ? "" : " ") + title + (title.isEmpty() ? "" : " ") + status);
    }
    
    private SmallAd querySmallAdDetails(SelenideElement smallAdElement)
    {
        // Lets see what's going on
        smallAdElement.scrollTo();

        // Get activation status
        boolean isActive = isActive(smallAdElement);
    	
        // Get id
        smallAdElement.shouldHave(attribute("data-adid"));
        String id = smallAdElement.getAttribute("data-adid").trim();
        
        // Get end date
        String endDate = smallAdElement.$(".manageaditem-main .manageaditem-ad .managead-listitem-enddate").should(exist).getText().trim();

        // Print the details
        printElementOperationStatus(smallAdElement, "(" + itemCounter + ")", "- id: " + id + ", active: " + isActive + ", end date: " + endDate);
        
        return null;
    }

    private SmallAd activateSmallAd(SelenideElement smallAdElement)
    {
        smallAdElement.$("a.managead-listitem-action-activate").should(exist).scrollTo().shouldBe(visible).click();
        
        printElementOperationStatus(smallAdElement, "Activated", "");

        return null;
    }

    private SmallAd deactivateSmallAd(SelenideElement smallAdElement)
    {
        smallAdElement.$("a.managead-listitem-action-deactivate").should(exist).scrollTo().shouldBe(visible).click();
        
        printElementOperationStatus(smallAdElement, "Deactivated", "");

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
        
        printElementOperationStatus(smallAdElement, "Deleted", "");

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
        
        printElementOperationStatus(smallAdElement, "Exported", "");

        return smallAd;
    }

    private void exportSmallAdDetails(SelenideElement smallAdElement, SmallAd smallAd)
    {
        // Open ad detail page
        smallAdElement.$("section.manageaditem-ad > h2 > a").should(exist).scrollTo().shouldBe(visible).click();

        // Create small ad detail page and export details
        new AdDetailsPage().exportAdDetails(smallAd);

        // Go back to small ad overview (keeps pagination in tact)
        Selenide.back();
    }
}
