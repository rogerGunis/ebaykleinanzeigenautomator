package de.unik.ebaykleinanzeigenautomator.pageobjects.pages;

public class Homepage extends BrowsingPage
{
    @Override
    public void validateIsExpectedPage()
    {
        super.validateIsExpectedPage();

        // $("#home").should(exist);
    }
}
