package de.unik.ebaykleinanzeigenautomator;

import de.unik.ebaykleinanzeigenautomator.flows.ChangeStatusOfAllSmallAdsFlow;
import de.unik.ebaykleinanzeigenautomator.flows.DeleteAllInactiveSmallAdsFlow;
import de.unik.ebaykleinanzeigenautomator.flows.PullSmallAdContainerFlow;
import de.unik.ebaykleinanzeigenautomator.util.Context;

public class App
{
    public static void main(String[] args)
    {
        Context.initialize();

        new DeleteAllInactiveSmallAdsFlow().run();

        new PullSmallAdContainerFlow().run().writeToDisk();

        new ChangeStatusOfAllSmallAdsFlow(false).run();

        new ChangeStatusOfAllSmallAdsFlow(true).run();
        
        /*
        SmallAdContainer smallAdContainer = new SmallAdContainer();
        if(!smallAdContainer.readFromDisk("1535486795672"))
        {
            return;
        }
            
        if(smallAdContainer.writeToDisk())
        {
            return;
        }
        */
        
        System.out.println("Everything ok");
    }
}