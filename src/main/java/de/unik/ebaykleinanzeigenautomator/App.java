package de.unik.ebaykleinanzeigenautomator;

import java.io.IOError;
import java.util.logging.Logger;

import de.unik.ebaykleinanzeigenautomator.datamodels.SmallAdContainer;
import de.unik.ebaykleinanzeigenautomator.flows.ChangeStatusOfAllSmallAdsFlow;
import de.unik.ebaykleinanzeigenautomator.flows.DeleteAllActiveSmallAdsFlow;
import de.unik.ebaykleinanzeigenautomator.flows.DeleteAllInactiveSmallAdsFlow;
import de.unik.ebaykleinanzeigenautomator.flows.ExportSmallAdContainerFlow;
import de.unik.ebaykleinanzeigenautomator.flows.ImportSmallAdContainerFlow;
import de.unik.ebaykleinanzeigenautomator.util.Context;

public class App
{
    private static final String INPUT_OUTPUT_ERROR = "An input/output error occured while trying to read from your input device.";
    private static final String INVALID_INPUT_ERROR = "Please choose between options 1 - 7. Enter the digit of the option you want. No other inputs are allowed.";

    private String inputString = null;

    private boolean exit = false;

    public App()
    {
        Context.initialize();
    }

    private void printTitle()
    {
        System.out.println("\n\nEbay Kleinanzeigen Automator");
        System.out.println("----------------------------\n");
    }

    private void printMainMenu()
    {
        printTitle();

        System.out.println("1) Set account credentials");
        System.out.println("2) Export all existing small ads from the site to your harddisk");
        System.out.println("3) Activate all small ads at the site");
        System.out.println("4) Deactivate all small ads at the site");
        System.out.println("5) Delete all inactive small ads at the site");
        System.out.println("6) Delete all active small ads at the site");
        System.out.println("7) Import all active small ads from your harddisk to the site");
        System.out.println("8) Exit\n");
        System.out.print("> ");
    }

    private void readInput(boolean isPassword)
    {
        try
        {
            inputString = "";
            if(!isPassword)
            {
                inputString = System.console().readLine();
            }
            else
            {
                inputString = String.valueOf(System.console().readPassword());
            }
        }
        catch (IOError e)
        {
            System.out.println("\n" + INPUT_OUTPUT_ERROR);
            System.out.println("Error was: " + e.getMessage());
        }
    }

    private void interpretInput()
    {
        // Convert input string to number
        int input = 0;
        try
        {
            input = Integer.parseInt(inputString);
        }
        catch (NumberFormatException nfe)
        {
            // Just ignore, we will handle it in our switch below
        }

        // Interpret
        switch (input)
        {
            case 1:
            {
                System.out.println("\nSetting credentials\n");
                readCredentials();
            }
                break;
            case 2:
            {
                System.out.println("\nExporting small ads\n");

                ExportSmallAdContainerFlow exportFlow = new ExportSmallAdContainerFlow();
                if (!exportFlow.run())
                {
                    return;
                }

                if (!exportFlow.getSmallAdContainer().writeToDisk(Context.get().getWorkingFilePath()))
                {
                    return;
                }
            }
                break;
            case 3:
            {
                System.out.println("\nActivating small ads\n");

                if (!new ChangeStatusOfAllSmallAdsFlow(true).run())
                {
                    return;
                }
            }
                break;
            case 4:
            {
                System.out.println("\nDeactivating small ads\n");

                if (!new ChangeStatusOfAllSmallAdsFlow(false).run())
                {
                    return;
                }
            }
                break;
            case 5:
            {
                System.out.println("\nDeleting inactive small ads\n");

                if (!new DeleteAllInactiveSmallAdsFlow().run())
                {
                    return;
                }
            }
                break;
            case 6:
            {
                System.out.println("\nDeleting active small ads\n");

                if (!new DeleteAllActiveSmallAdsFlow().run())
                {
                    return;
                }
            }
                break;
            case 7:
            {
                System.out.println("\nImporting active small ads\n");

                SmallAdContainer smallAdContainer = new SmallAdContainer();
                if (!smallAdContainer.readFromDisk(Context.get().getWorkingFilePath()))
                {
                    return;
                }

                if (!new ImportSmallAdContainerFlow(smallAdContainer).run())
                {
                    return;
                }
            }
                break;
            case 8:
            {
                System.out.println("\nExiting");
                exit = true;
            }
                break;
            default:
            {
                System.out.println("\n" + INVALID_INPUT_ERROR);
                return;
            }
        }

        System.out.println("\nEverything ok");
    }

    public void readCredentials()
    {
        System.out.println("The following inputs will not be save to harddisk.\n");

        System.out.print("Please enter your username (email) for ebay-kleinanzeigen.de and press enter: ");
        readInput(false);
        String username = inputString;

        System.out.print("Please enter your password for ebay-kleinanzeigen.de and press enter: ");
        readInput(true);
        String password = inputString;

        Context.get().setAccount(username, password);
    }

    public void mainLoop()
    {
        do
        {
            printMainMenu();
            readInput(false);
            interpretInput();
        }
        while (!exit);
    }

    public void run()
    {
        // JUL deactivate logger
        Logger logger = Logger.getLogger("");
        logger.removeHandler(logger.getHandlers()[0]);
        logger.setUseParentHandlers(false);
        
        printTitle();
        readCredentials();
        mainLoop();
    }

    public static void main(String[] args)
    {
        /*
         * TODOs
         * 
         * - Fix general issue during delete (counter??)
         * - Fix issue when list of small ads is empty but operation should be executed (seen on delete)
         * - Fix issue on import: Element not found {#pstad-cnfrm}
         * - Add feature to import only missing small ads
         * - Mute Selenium/Selenide/.. logger output in command line output
         * - Automate packaging / JAR
         */

        new App().run();
    }
}