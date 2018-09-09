package de.unik.ebaykleinanzeigenautomator.datamodels;

import org.apache.commons.lang3.StringUtils;

import de.unik.ebaykleinanzeigenautomator.util.Context;

public class Account
{
    public String username = null;

    public String password = null;
    
    public Account()
    {
        if(Context.get().getConfiguration().credentialsFromConfiguration())
        {
            username = Context.get().getConfiguration().accountUsername();
            password = Context.get().getConfiguration().accountPassword();
        }
    }

    public boolean isInitialized()
    {
        return !StringUtils.isBlank(username) && !StringUtils.isBlank(password);
    }
}
