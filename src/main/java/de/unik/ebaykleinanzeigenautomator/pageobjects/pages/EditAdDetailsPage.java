package de.unik.ebaykleinanzeigenautomator.pageobjects.pages;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.By;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import de.unik.ebaykleinanzeigenautomator.datamodels.SmallAd;
import de.unik.ebaykleinanzeigenautomator.util.Context;

public class EditAdDetailsPage extends BrowsingPage
{
    @Override
    public void validateIsExpectedPage()
    {
        super.validateIsExpectedPage();

        $("#pstad").should(exist);
    }

    public PostAdConfirmPage publishAd(SmallAd smallAd)
    {
        selectAttributesIfPossible(smallAd);
        setType(smallAd);
        setTitle(smallAd);
        setContent(smallAd);
        setPriceIfPossible(smallAd);
        uploadImagesIfPossible(smallAd);

        return clickSubmit();
    }

    public void selectAttributesIfPossible(SmallAd smallAd)
    {
        Hashtable<String, String> attributes = smallAd.attributes;
        if (attributes.isEmpty())
        {
            return;
        }

        SelenideElement attributeContainer = $("#postad-attributes").should(exist);

        ElementsCollection attributeRows = attributeContainer.findAll("div.pstad-attrs > label[class^='formgroup-label']");
        attributeRows.shouldHave(sizeGreaterThanOrEqual(attributes.size()));

        Iterator<String> attributeKeysIterator = attributes.keySet().iterator();
        while (attributeKeysIterator.hasNext())
        {
            String attributeKey = attributeKeysIterator.next();

            String attributeId = attributeRows.find(matchText(attributeKey)).shouldBe(visible).shouldHave(attribute("for")).getAttribute("for");

            $(By.id(attributeId)).selectOptionContainingText(attributes.get(attributeKey));
        }
    }

    public void setType(SmallAd smallAd)
    {
        if (smallAd.isOffer)
        {
            $("input#adType1[value='OFFER']").shouldBe(visible).scrollTo().setSelected(true);
        }
        else
        {
            $("input#adType2[value='WANTED']").shouldBe(visible).scrollTo().setSelected(true);
        }
    }

    public void setTitle(SmallAd smallAd)
    {
        $("#postad-title").shouldBe(visible).setValue(smallAd.title);
    }

    public void setContent(SmallAd smallAd)
    {
        $("#pstad-descrptn").shouldBe(visible).setValue(smallAd.content);
    }

    public void setPriceIfPossible(SmallAd smallAd)
    {
        if (!smallAd.hasNoPrice)
        {
            if (smallAd.isForFree)
            {
                $("input#priceType3[value='GIVE_AWAY']").shouldBe(visible).scrollTo().setSelected(true);
            }
            else
            {
                $("#pstad-price").shouldBe(visible).scrollTo().setValue(smallAd.price);

                if (smallAd.isFixedPrice)
                {
                    $("input#priceType1[value='FIXED']").shouldBe(visible).scrollTo().setSelected(true);
                }
                else
                {
                    $("input#priceType2[value='NEGOTIABLE']").shouldBe(visible).scrollTo().setSelected(true);
                }
            }
        }
    }

    public void uploadImagesIfPossible(SmallAd smallAd)
    {
        SelenideElement uploadElement = $("input[id^='html5'][type='file']").should(exist);
        List<String> images = smallAd.images;

        for (int i = 0; i < images.size(); i++)
        {
            // Upload and wait for each image
            uploadElement.uploadFile(new File(Context.get().getWorkingDirectoryPath() + images.get(i)));
            $$("#j-pictureupload-thumbnails img").shouldHaveSize(i + 1);
        }
    }

    public PostAdConfirmPage clickSubmit()
    {
        $("#pstad-submit").scrollTo().shouldBe(visible).click();

        return new PostAdConfirmPage();
    }
}
