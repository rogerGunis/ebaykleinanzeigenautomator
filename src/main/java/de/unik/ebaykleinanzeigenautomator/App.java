package de.unik.ebaykleinanzeigenautomator;

import de.unik.ebaykleinanzeigenautomator.flows.ChangeStatusOfAllSmallAdsFlow;
import de.unik.ebaykleinanzeigenautomator.flows.DeleteAllActiveSmallAdsFlow;
import de.unik.ebaykleinanzeigenautomator.flows.PullSmallAdContainerFlow;
import de.unik.ebaykleinanzeigenautomator.util.Context;

public class App 
{
    public static void main( String[] args )
    {
    		Context.initialize();

    		new PullSmallAdContainerFlow().run().writeToDisk();
    		
    		//new DeleteAllActiveSmallAdsFlow().run();
    		
    		new ChangeStatusOfAllSmallAdsFlow(false).run();

        Context.get().resetSessionIdentifier();
    		new PullSmallAdContainerFlow().run().writeToDisk();
    		
    		new ChangeStatusOfAllSmallAdsFlow(true).run();
    		
        Context.get().resetSessionIdentifier();
    		new PullSmallAdContainerFlow().run().writeToDisk();
    }
}