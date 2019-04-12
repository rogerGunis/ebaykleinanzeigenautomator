package de.unik.ebaykleinanzeigenautomator;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;

import de.unik.ebaykleinanzeigenautomator.datamodels.SmallAdContainer;
import de.unik.ebaykleinanzeigenautomator.flows.ChangeSmallAdsStatusFlow;
import de.unik.ebaykleinanzeigenautomator.flows.DeleteSmallAdsFlow;
import de.unik.ebaykleinanzeigenautomator.flows.ExportSmallAdContainerFlow;
import de.unik.ebaykleinanzeigenautomator.flows.ImportSmallAdContainerFlow;
import de.unik.ebaykleinanzeigenautomator.flows.LoginLogoutFlow;
import de.unik.ebaykleinanzeigenautomator.util.Context;

public class App
{
    private static final String INPUT_OUTPUT_ERROR = "An input/output error occured while trying to read from your input device.";
    private static final String INVALID_INPUT_ERROR = "Please choose between options 0 - 9. Enter the digit of the option you want. No other inputs are allowed.";

    private static final String COMMANDLINE_PARAMETER_SESSION = "-session=";
    
    private boolean exit = false;

    public App(String args[])
    {
        Context.initialize();
        
        for(int i=0; i<args.length; i++)
        {
            processCommandLineParameter(args[i]);
        }
    }
    
    private void processCommandLineParameter(String parameter)
    {
        if(parameter.toLowerCase().startsWith(COMMANDLINE_PARAMETER_SESSION))
        {
            String sessionIdentifier = parameter.replaceAll(COMMANDLINE_PARAMETER_SESSION, "").trim();
            Context.get().resetSessionIdentifier(sessionIdentifier);
            
            System.out.println("\nUsing session identifier: " + Context.get().getSessionIdentifier());
        }
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
        System.out.println("5) Delete inactive small ads at the site");
        System.out.println("6) Delete active small ads at the site");
        System.out.println("7) Import active small ads from your harddisk to the site");
        System.out.println("8) Import active and not yet existing small ads from your harddisk to the site");
        System.out.println("9) Import all small ads from your harddisk to the site");
        System.out.println("0) Exit\n");
        System.out.print("> ");
    }

    private String readInput(boolean isPassword)
    {
    	String inputString = null;
    	
        if(!Context.get().getConfiguration().systemConsoleInput())
        {
        	try
        	{
        		// No need to close System.in as it is created by the JVM
        		inputString = new BufferedReader(new InputStreamReader(System.in)).readLine();
        	}
            catch (IOException ioe)
            {
                System.out.println("\n" + INPUT_OUTPUT_ERROR);
                System.out.println("Error was: " + ioe.toString());
            }
        }
        else
        {
        	try
        	{
                if(!isPassword)
                {
                    inputString = System.console().readLine();
                }
                else
                {
                    inputString = String.valueOf(System.console().readPassword());
                }
        	}
            catch(IOError ioe)
            {
                System.out.println("\n" + INPUT_OUTPUT_ERROR);
                System.out.println("Error was: " + ioe.toString());
            }
        }
        
        return inputString;
    }

    private void handleMainMenuInput()
    {
    	// Grab input and convert to integer
        int input = 0;
        try
        {
            input = Integer.parseInt(readInput(false));
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
                    System.out.println("\nSetting and verifying credentials\n");

                    readCredentials();
                    
                    if(!new LoginLogoutFlow().run())
                    {
                        return;
                    }
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
    
                    if (!new ChangeSmallAdsStatusFlow().run(true))
                    {
                        return;
                    }
                }
                break;
            case 4:
                {
                    System.out.println("\nDeactivating small ads\n");
    
                    if (!new ChangeSmallAdsStatusFlow().run(false))
                    {
                        return;
                    }
                }
                break;
            case 5:
                {
                    System.out.println("\nDeleting inactive small ads\n");
    
                    if (!new DeleteSmallAdsFlow().run(false))
                    {
                        return;
                    }
                }
                break;
            case 6:
                {
                    System.out.println("\nDeleting active small ads\n");
    
                    if (!new DeleteSmallAdsFlow().run(true))
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
    
                    if (!new ImportSmallAdContainerFlow(smallAdContainer).run(true, false))
                    {
                        return;
                    }
                }
                break;
            case 8:
                {
                    System.out.println("\nImporting active and not yet existing small ads\n");
    
                    SmallAdContainer smallAdContainer = new SmallAdContainer();
                    if (!smallAdContainer.readFromDisk(Context.get().getWorkingFilePath()))
                    {
                        return;
                    }
    
                    if (!new ImportSmallAdContainerFlow(smallAdContainer).run(true, true))
                    {
                        return;
                    }
                }
                break;
            case 9:
                {
                    System.out.println("\nImporting all small ads\n");
    
                    SmallAdContainer smallAdContainer = new SmallAdContainer();
                    if (!smallAdContainer.readFromDisk(Context.get().getWorkingFilePath()))
                    {
                        return;
                    }
    
                    if (!new ImportSmallAdContainerFlow(smallAdContainer).run(false, false))
                    {
                        return;
                    }
                }
                break;
            case 0:
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
        System.out.println("The following inputs will not be saved to harddisk.\n");

        System.out.print("Please enter your username (email) for ebay-kleinanzeigen.de and press enter: ");
        String username = readInput(false);

        System.out.print("Please enter your password for ebay-kleinanzeigen.de and press enter: ");
        String password = readInput(true);
        
        System.out.println("");

        Context.get().setAccount(username, password);
    }

    public void mainLoop()
    {
        do
        {
            printMainMenu();
            handleMainMenuInput();
        }
        while (!exit);
    }

    public void run()
    {
        if(!Context.get().getConfiguration().credentialsFromConfiguration())
        {
            printTitle();
            readCredentials();
            new LoginLogoutFlow().run();
        }
        
        mainLoop();
    }

    public static void main(String[] args)
    {
        new App(args).run();
    }
}