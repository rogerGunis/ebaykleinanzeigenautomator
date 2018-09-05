package de.unik.ebaykleinanzeigenautomator;

import de.unik.ebaykleinanzeigenautomator.flows.ExportSmallAdContainerFlow;
import de.unik.ebaykleinanzeigenautomator.util.Context;

public class App
{
    public static void main(String[] args)
    {
        Context.initialize();

        /*
         * if(!new ChangeStatusOfAllSmallAdsFlow(false).run()) { return; }
         * 
         * if(!new ChangeStatusOfAllSmallAdsFlow(true).run()) { return; } //
         */

        // *
        ExportSmallAdContainerFlow exportFlow = new ExportSmallAdContainerFlow();
        if (!exportFlow.run())
        {
            return;
        }

        if (!exportFlow.getSmallAdContainer().writeToDisk(Context.get().getWorkingFilePath()))
        {
            return;
        }
        // */

        /*
         * SmallAdContainer smallAdContainer = new SmallAdContainer(); if(!smallAdContainer.readFromDisk(Context.get().getWorkingFilePath())) { return; }
         * 
         * if(!new ImportSmallAdContainerFlow(smallAdContainer).run()) { return; } //
         */

        System.out.println("Everything ok");
    }
}