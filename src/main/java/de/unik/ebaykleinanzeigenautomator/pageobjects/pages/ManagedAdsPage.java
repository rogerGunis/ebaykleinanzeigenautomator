package de.unik.ebaykleinanzeigenautomator.pageobjects.pages;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Selenide;
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
        processSmallAds("Activated", null, s -> isInactive(s), s -> activateSmallAd(s), false);
    }

    public void deactivateAllSmallAds()
    {
        processSmallAds("Deactivated", null, s -> isActive(s), s -> deactivateSmallAd(s), false);
    }

    public void deleteAllInactiveSmallAds()
    {
        processSmallAds("Deleted", null, s -> isInactive(s), s -> deleteSmallAd(s), true);
    }

    public void deleteAllActiveSmallAds()
    {
        processSmallAds("Deleted", null, s -> isActive(s), s -> deleteSmallAd(s), true);
    }

    public void exportAllSmallAds(SmallAdContainer smallAdContainer)
    {
        processSmallAds("Exported", smallAdContainer, s -> { return true; }, s -> exportSmallAd(s), false);
    }

    private void processSmallAds(String operation, SmallAdContainer smallAdContainer, Predicate<SelenideElement> predicate, Function<SelenideElement, SmallAd> function, boolean modifiesItemList)
    {
        // Validate
        itemList.findAll("li.cardbox").shouldHave(CollectionCondition.sizeGreaterThan(0));

        boolean applied = false;
        int itemCount = itemList.findAll("li.cardbox").size();
        int i = 0;

        while (i < itemCount)
        {
            // Re-evaluate elements collection each iteration
            SelenideElement currentSmallAdElement = itemList.findAll("li.cardbox").get(i).should(exist);

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

                // Print status
                System.out.println(operation + " '" + currentSmallAdElement.find(".manageaditem-ad a").shouldBe(visible).text() + "'");

                // Indicate that we could execute our operation at least once
                applied = true;

                if (!modifiesItemList)
                {
                    // Increase counter only if operation does not modify our item list
                    i++;
                }
                else
                {
                    // If the operation modified our item list, we now have fewer items
                    itemCount--;

                    // Validate that list changed
                    itemList.findAll("li.cardbox").shouldHaveSize(itemCount);
                }
            }
            else
            {
                // Increase counter since nothing happened anyway
                i++;
            }

            // Check if we need to page
            if (i == itemCount)
            {
                // Check for pagination
                SelenideElement paginationNext = $(".pagination-pages > .pagination-current~a.pagination-page");
                if (paginationNext.is(visible))
                {
                    // Get page number of next page
                    String pageNumber = paginationNext.getAttribute("data-page");

                    // Go to next page
                    paginationNext.shouldBe(visible).scrollTo().click();

                    // Wait for page number to show up as current page
                    $(".pagination-pages > .pagination-current").shouldHave(exactText(pageNumber));

                    // Retrieve new item list information and reset counter
                    itemCount = itemList.findAll("li.cardbox").size();
                    i = 0;
                }
            }
        }

        if (!applied)
        {
            System.out.println("No applicable small ads found in account '" + Context.get().getAccount().username + "'");
        }
    }

    private boolean isInactive(SelenideElement smallAdElement)
    {
        smallAdElement.$("a.managead-listitem-action-activate.is-hidden, a.managead-listitem-action-deactivate.is-hidden").should(exist);

        return smallAdElement.$("a.managead-listitem-action-activate").is(visible);
    }

    private SmallAd activateSmallAd(SelenideElement smallAdElement)
    {
        smallAdElement.$("a.managead-listitem-action-activate").shouldBe(visible).scrollTo().click();

        return null;
    }

    private boolean isActive(SelenideElement smallAdElement)
    {
        smallAdElement.$("a.managead-listitem-action-activate.is-hidden, a.managead-listitem-action-deactivate.is-hidden").should(exist);

        return smallAdElement.$("a.managead-listitem-action-deactivate").is(visible);
    }

    private SmallAd deactivateSmallAd(SelenideElement smallAdElement)
    {
        smallAdElement.$("a.managead-listitem-action-deactivate").shouldBe(visible).scrollTo().click();

        return null;
    }

    private SmallAd deleteSmallAd(SelenideElement smallAdElement)
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

    private SmallAd exportSmallAd(SelenideElement smallAdElement)
    {
        SmallAd smallAd = new SmallAd();

        // Lets see whats going on
        smallAdElement.scrollTo();

        // Get activation status
        smallAd.isActive = isActive(smallAdElement);

        // Get identifier
        smallAdElement.shouldHave(attribute("data-adid"));
        String id = smallAdElement.getAttribute("data-adid");
        Assert.assertTrue("Ad id must exist and not be empty.", StringUtils.isNotBlank(id));
        smallAd.id = id;

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
        smallAdElement.$("section.manageaditem-ad > h2 > a").shouldBe(visible).scrollTo().click();

        // Create small ad detail page and export details
        AdDetailsPage adDetailPage = new AdDetailsPage();
        adDetailPage.exportAdDetails(smallAd);

        // Go back to small ad overview (keeps pagination in tact)
        Selenide.back();
    }
}
