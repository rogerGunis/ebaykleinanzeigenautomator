package de.unik.ebaykleinanzeigenautomator.pageobjects.pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import de.unik.ebaykleinanzeigenautomator.datamodels.SmallAd;
import de.unik.ebaykleinanzeigenautomator.util.Context;
import org.openqa.selenium.By;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class AdDetailsPage extends BrowsingPage
{
    @Override
    public void validateIsExpectedPage()
    {
        super.validateIsExpectedPage();

        $("#vap").should(exist);
    }

    public void exportAdDetails(SmallAd smallAd)
    {
        exportDetails(smallAd);
        exportCategories(smallAd.categories);
//        exportAttributes(smallAd.attributes);

        if (Context.get().getConfiguration().projectDownloadImages() && $("#viewad-images").exists())
        {
            // Open image zoom container
            $("#viewad-image.is-clickable").scrollTo().shouldBe(visible).click();

            // Store image information
            exportImages(smallAd.id, smallAd.images);

            // Close image zoom container
            $("#viewad-lightbox a.mfp-close").scrollTo().shouldBe(visible).click();
        }
    }

    private void exportDetails(SmallAd smallAd)
    {
        smallAd.isOffer = !$(By.xpath("//script[contains(., 'WANTED')]")).exists();
        smallAd.title = $("#viewad-title").shouldBe(visible).text();
        smallAd.location = $("#viewad-locality").shouldBe(visible).text();
        smallAd.creationDate = $("#viewad-extra-info > div:nth-child(1) > span").text().trim();
        smallAd.content = $("#viewad-description-text").shouldBe(visible).text();

        if ($("#viewad-price").exists())
        {
            smallAd.price = $("#viewad-main-info").findElement(By.cssSelector("meta[itemprop=\"price\"]")).getAttribute("content");
            smallAd.price = smallAd.price.replaceAll("\\.00", "");

            if (!smallAd.price.isEmpty())
            {
                smallAd.isFixedPrice = $("#viewad-price").should(exist).text().contains("VB") ? false : true;
                smallAd.isForFree = false;
                smallAd.hasNoPrice = false;
            }
            else
            {
                smallAd.isFixedPrice = false;
                smallAd.isForFree = true;
                smallAd.hasNoPrice = false;
            }
        }
        else
        {
            smallAd.price = "";
            smallAd.isFixedPrice = false;
            smallAd.isForFree = false;
            smallAd.hasNoPrice = true;
        }
    }

    private void exportCategories(List<String> categories)
    {
        ElementsCollection breadCrumb = $$("#vap-brdcrmb > .breadcrump-link");
        breadCrumb.shouldHave(CollectionCondition.sizeGreaterThan(0));

        // Loop through bread crumb items but ignore first one
        for (int i = 1; i < breadCrumb.size(); i++)
        {
            String category = breadCrumb.get(i).find("span[itemprop=title]").should(exist).text();
            categories.add(category.trim());
        }
    }

    private void exportAttributes(Hashtable<String, String> attributes)
    {
        ElementsCollection attributeKeys = $$("#viewad-details .attributelist--key").shouldHave(sizeGreaterThanOrEqual(2));
        ElementsCollection attributeValues = $$("#viewad-details .attributelist--value").shouldHave(sizeGreaterThanOrEqual(2));
        
        // Ads with only two attributes are currently in processing (still missing creation date)
        // We will ignore such ads here and only handle complete ads with 3+ attributes
        
        if(attributeKeys.size() > 2)
        {
            // Attributes with index 0-2 are location, creation date and ad id and will be ignored here
            for (int i = 3; i < attributeKeys.size(); i++)
            {
                String key = attributeKeys.get(i).text().trim().replace(":", "");
                String value = attributeValues.get(i).find("span").text().trim();
    
                attributes.put(key, value);
            }
        }
    }

    private void exportImages(String id, List<String> images)
    {
        // Some validation
        SelenideElement imageContainer = $("#viewad-lightbox").shouldBe(visible);
        ElementsCollection imageElements = imageContainer.findAll(".ad-image-wrapper .ad-image > img").shouldHave(CollectionCondition.sizeGreaterThan(0));

        // Switch to data download folder
        com.codeborne.selenide.Configuration.reportsFolder = Context.get().getWorkingDirectoryPath();

        // Loop through all available images
        for (int i = 0; i < imageElements.size(); i++)
        {
            SelenideElement imageElement = imageElements.get(i);
            imageElement.shouldHave(attribute("src"));

            String imageUrl = imageElement.getAttribute("src");
            try
            {
                // Trigger download
                File imageFile = Selenide.download(imageUrl, Context.get().getConfiguration().selenideFileDownloadTimeout());

                // Extract image file extension from URL
                String extension = "." + imageUrl.substring(imageUrl.length() - 3, imageUrl.length());

                // Set new filename
                File newFile = new File(com.codeborne.selenide.Configuration.reportsFolder + "/" + id + "-" + i + extension);

                // Rename loaded file to new filename
                imageFile.renameTo(newFile);
                
                // Selenide will create a directory per image download which we will now remove 
                File remainingDownloadDirectory = new File(imageFile.getPath().replace(imageFile.getName(), ""));
                remainingDownloadDirectory.delete();

                // Store filename
                images.add(newFile.getName());
            }
            catch (IOException e)
            {
                System.out.print("Failed to download image from " + imageUrl);
                System.out.print("Error was: " + e.toString());

                if (Context.get().getConfiguration().projectDebug())
                {
                    e.printStackTrace();
                }
            }
        }

        // Restore Selenide reports folder
        com.codeborne.selenide.Configuration.reportsFolder = Context.get().getConfiguration().selenideReportsFolder();
    }
}
