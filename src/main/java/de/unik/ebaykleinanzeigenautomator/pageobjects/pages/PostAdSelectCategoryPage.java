package de.unik.ebaykleinanzeigenautomator.pageobjects.pages;

import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import java.util.Hashtable;
import java.util.List;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import de.unik.ebaykleinanzeigenautomator.datamodels.SmallAd;

public class PostAdSelectCategoryPage extends BrowsingPage
{
    @Override
    public void validateIsExpectedPage()
    {
        super.validateIsExpectedPage();

        $("#postad-step1").should(exist);
    }

    public void selectCategories(SmallAd smallAd)
    {
        List<String> categories = smallAd.categories;
        SelenideElement selectedCategory = null;
        int i = 0;

        for (; i < categories.size(); i++)
        {
            selectedCategory = selectCategory(categories.get(i), i);
        }

        while (!selectedCategory.parent().has(cssClass("is-leaf")))
        {
            selectedCategory = selectCategoryViaAttributes(smallAd.attributes, i++);
        }
    }

    public EditAdDetailsPage clickNext()
    {
        $("#postad-step1-frm button").shouldBe(visible).click();

        return new EditAdDetailsPage();
    }

    private SelenideElement selectCategory(String categoryName, int columnIndex)
    {
        // Get categories of column
        ElementsCollection categories = getCategoriesOfColumn(columnIndex);

        // Get the category with given name
        SelenideElement category = categories.find(exactText(categoryName)).shouldBe(visible);
        category.scrollTo().click();

        // Validate that the category was set
        category.parent().shouldHave(cssClass("is-active"));

        // Return selected category
        return category;
    }

    private SelenideElement selectCategoryViaAttributes(Hashtable<String, String> attributes, int columnIndex)
    {
        // Get categories of column
        ElementsCollection categories = getCategoriesOfColumn(columnIndex);

        // Get column headline which acts as key, remove unnecessary characters
        String attributeKey = getCategoryColumnHeadline(columnIndex);
        attributeKey = attributeKey.replace("*", "").trim();

        // Get value mapped to attribute key
        String attributeValue = attributes.get(attributeKey);
        if (attributeValue == null)
        {
            throw new RuntimeException("Failed to find attribute category '" + attributeKey + "'");
        }

        // Get the category with given name
        SelenideElement category = categories.find(exactText(attributeValue)).shouldBe(visible);
        category.scrollTo().click();

        // Validate that the category was set
        category.parent().shouldHave(cssClass("is-active"));

        // Return selected category
        return category;
    }

    private ElementsCollection getCategoriesOfColumn(int columnIndex)
    {
        // Get category list column with given index
        SelenideElement categoryListColumn = getCategoryColumn(columnIndex);

        // Get all categories of category column
        ElementsCollection categories = categoryListColumn.findAll("ul.category-selection-list li.category-selection-list-item > a.category-selection-list-item-link");
        categories.shouldHave(CollectionCondition.sizeGreaterThan(0));

        // Return all categories of column with index
        return categories;
    }

    private String getCategoryColumnHeadline(int columnIndex)
    {
        // Get category list column with given index
        SelenideElement categoryListColumn = getCategoryColumn(columnIndex);

        // Return headline of category column
        return categoryListColumn.find("h2.sectionheadline").should(exist).text();
    }

    private SelenideElement getCategoryColumn(int columnIndex)
    {
        // Get all category columns and return the one with the given index
        ElementsCollection categoryListColumns = $$("#postad-category-select-box .category-selection-col");
        return categoryListColumns.shouldHaveSize(columnIndex + 1).get(columnIndex).should(exist);
    }
}
