package de.unik.ebaykleinanzeigenautomator.pageobjects.components;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;

import de.unik.ebaykleinanzeigenautomator.pageobjects.Component;

public class Pagination extends Component
{
    SelenideElement pagination = $(".pagination-pages");

    SelenideElement paginationNext = pagination.$(".pagination-current~a.pagination-page");

    @Override
    public void validateComponentIsAvailable()
    {
        pagination.should(exist);
    }

    public void apply()
    {
        // Get page number of next page
        String pageNumber = paginationNext.getAttribute("data-page");

        // Go to next page
        paginationNext.click();

        // Wait for page number to show up as current page
        $(".pagination-pages > .pagination-current").should(exist).shouldHave(exactText(pageNumber));
    }

    public boolean isPossible()
    {
        pagination.should(exist).scrollTo();
        return paginationNext.isDisplayed();
    }
}
