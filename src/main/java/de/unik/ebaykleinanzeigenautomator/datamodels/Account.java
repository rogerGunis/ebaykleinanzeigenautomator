package de.unik.ebaykleinanzeigenautomator.datamodels;

import org.apache.commons.lang3.StringUtils;

public class Account
{
    public String username = null;

    public String password = null;

    public boolean isInitialized()
    {
        return !StringUtils.isBlank(username) && !StringUtils.isBlank(password);
    }
}
