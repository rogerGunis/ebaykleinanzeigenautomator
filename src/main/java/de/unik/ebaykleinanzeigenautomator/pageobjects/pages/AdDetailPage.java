package de.unik.ebaykleinanzeigenautomator.pageobjects.pages;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import de.unik.ebaykleinanzeigenautomator.models.SmallAd;
import de.unik.ebaykleinanzeigenautomator.util.Context;


public class AdDetailPage extends BrowsingPage
{
	@Override
	public void validateIsExpectedPage()
	{
		super.validateIsExpectedPage();
		
		$("#vap").should(exist);
	}
	
	public void pullAdDetails(SmallAd smallAd)
	{
		pullDetails(smallAd);
		pullCategories(smallAd.categories);
		pullAttributes(smallAd.attributes);
		
		if($("#viewad-images").exists())
		{
			// Open image zoom container
			$("#viewad-image.is-clickable").shouldBe(visible).scrollTo().click();
			
			// Store image information
			pullImages(smallAd.id, smallAd.images);
			
			// Close image zoom container
			$("#viewad-lightbox a.mfp-close").shouldBe(visible).scrollTo().click();
		}
	}
	
	private void pullDetails(SmallAd smallAd)
	{
		smallAd.title = $("#viewad-title").shouldBe(visible).text();
		smallAd.location = $("#viewad-locality").shouldBe(visible).text();
		smallAd.creationDate = $("#viewad-details dl dd.attributelist--value:nth-of-type(2)").shouldBe(visible).text();
		smallAd.content = $("#viewad-description-text").shouldBe(visible).text();
		
		String price = $("#viewad-details section.l-container meta[itemprop='price']").should(exist).getAttribute("content");
		if(!price.trim().isEmpty())
		{
			smallAd.price = price;
			smallAd.isFixedPrice = $("#viewad-price").should(exist).text().contains("VB") ? false : true;
			smallAd.hasNoPrice = false;
		}
		else
		{
			smallAd.price = "";
			smallAd.isFixedPrice = false;
			smallAd.hasNoPrice = true;
		}
	}
	
	private void pullCategories(List<String> categories)
	{
		ElementsCollection breadCrumb = $$("#vap-brdcrmb > .breadcrump-link");
		breadCrumb.shouldHave(CollectionCondition.sizeGreaterThan(0));
		
		// Loop through bread crumb items but ignore first one
		for(int i=1; i<breadCrumb.size(); i++)
		{
			String category = breadCrumb.get(i).find("span[itemprop=title]").should(exist).text();
			categories.add(category.trim());
		}
	}
	
	private void pullAttributes(Hashtable<String, String> attributes)
	{
		ElementsCollection attributeKeys = $$("#viewad-details .attributelist--key").shouldHave(sizeGreaterThanOrEqual(3));
		ElementsCollection attributeValues = $$("#viewad-details .attributelist--value").shouldHave(sizeGreaterThanOrEqual(3));
		
		// Attributes with index 0-2 are location, creation date and ad id and will be ignored here
		for(int i=3; i<attributeKeys.size(); i++)
		{
			String key = attributeKeys.get(i).text().trim().replace(":", "");
			String value = attributeValues.get(i).find("span").text().trim();
			
			attributes.put(key, value);
		}
	}

	private void pullImages(String id, List<String> images)
	{
		// Some validation
		SelenideElement imageContainer = $("#viewad-lightbox").shouldBe(visible);
		ElementsCollection imageElements = imageContainer.findAll(".ad-image-wrapper .ad-image > img").shouldHave(CollectionCondition.sizeGreaterThan(0));
		
		// Switch to data download folder
		com.codeborne.selenide.Configuration.reportsFolder = Context.get().getPullPath();
		
		// Loop through all available images
		for(int i=0; i<imageElements.size(); i++)
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
				
				// Store filename
				images.add(newFile.getName());
			}
			catch (IOException e)
			{
				System.out.print("Failed to download image from " + imageUrl);
				e.printStackTrace();
			}
		}
		
		// Restore Selenide reports folder
		com.codeborne.selenide.Configuration.reportsFolder = Context.get().getConfiguration().selenideReportsFolder();
	}
}
