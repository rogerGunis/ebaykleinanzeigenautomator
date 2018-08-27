package de.unik.ebaykleinanzeigenautomator;

import de.unik.ebaykleinanzeigenautomator.flows.PullSmallAdContainerFlow;
import de.unik.ebaykleinanzeigenautomator.util.Context;

public class App 
{
    public static void main( String[] args )
    {
    		Context.initialize();

    		PullSmallAdContainerFlow.run();
    }
}
