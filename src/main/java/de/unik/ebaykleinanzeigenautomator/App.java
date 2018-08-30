package de.unik.ebaykleinanzeigenautomator;

import de.unik.ebaykleinanzeigenautomator.datamodels.SmallAdContainer;
import de.unik.ebaykleinanzeigenautomator.flows.ImportSmallAdContainerFlow;
import de.unik.ebaykleinanzeigenautomator.util.Context;

public class App
{
    public static void main(String[] args)
    {
        Context.initialize();

        /*
        ExportSmallAdContainerFlow exportFlow = new ExportSmallAdContainerFlow();
        if(!exportFlow.run())
        {
            return;
        }
        
        if(!exportFlow.getSmallAdContainer().writeToDisk())
        {
            return;
        }
        //*/

        //*
        SmallAdContainer smallAdContainer = new SmallAdContainer();
        if(smallAdContainer.readFromDisk("1535657212486"))
        {
            new ImportSmallAdContainerFlow(smallAdContainer).run();
        }
        //*/

        System.out.println("Everything ok");
    }
}