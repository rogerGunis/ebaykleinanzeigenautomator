package de.unik.ebaykleinanzeigenautomator.pageobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public abstract class PageObject
{
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public abstract void validateIsExpectedPage();
}
