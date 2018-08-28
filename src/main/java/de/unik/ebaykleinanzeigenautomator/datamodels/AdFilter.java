package de.unik.ebaykleinanzeigenautomator.datamodels;

public class AdFilter
{
    public boolean activeOnly;

    public AdFilter(boolean activeOnly)
    {
        this.activeOnly = activeOnly;
    }

    public static AdFilter emptyFilter()
    {
        return new AdFilter(false);
    }
}
