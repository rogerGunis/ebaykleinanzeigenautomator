package de.unik.ebaykleinanzeigenautomator.datamodels;

import de.unik.ebaykleinanzeigenautomator.util.Context;

public class Account
{
	public String username;
	
	public String password;
	
	public Account()
	{
		// Initialize from configuration for now
		username = Context.get().getConfiguration().accountUsername();
		password = Context.get().getConfiguration().accountPassword();
	}
}
