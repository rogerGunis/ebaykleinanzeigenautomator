package de.unik.ebaykleinanzeigenautomator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;

import de.unik.ebaykleinanzeigenautomator.util.Configuration;
import org.apache.commons.lang3.StringUtils;

import de.unik.ebaykleinanzeigenautomator.datamodels.SmallAdContainer;
import de.unik.ebaykleinanzeigenautomator.flows.ChangeSmallAdsStatusFlow;
import de.unik.ebaykleinanzeigenautomator.flows.DeleteSmallAdsFlow;
import de.unik.ebaykleinanzeigenautomator.flows.ExportSmallAdContainerFlow;
import de.unik.ebaykleinanzeigenautomator.flows.ImportSmallAdContainerFlow;
import de.unik.ebaykleinanzeigenautomator.flows.LoginLogoutFlow;
import de.unik.ebaykleinanzeigenautomator.flows.QuerySmallAdsFlow;
import de.unik.ebaykleinanzeigenautomator.util.Context;

public class App
{
    private static final String INPUT_OUTPUT_ERROR = "An input/output error occured while trying to read from your input device.";

    private static final String INVALID_INPUT_ERROR = "Please choose between options 0 - B. Enter the character of the option you want. No other inputs are allowed.";

    private static final String COMMANDLINE_PARAMETER_SESSION = "-session=";

    private boolean exit = false;
    private final String outputPath;

    public App()
    {
        Context.initialize();
        outputPath = Context.get().getWorkingFilePath();
    }

    public void processCommandLineParameter(String parameter)
    {
        if(parameter.toLowerCase().startsWith(COMMANDLINE_PARAMETER_SESSION))
        {
        	handleSessionIdentifierReset(parameter.replaceAll(COMMANDLINE_PARAMETER_SESSION, "").trim());
        }
    }

    public void printTitle()
    {
    	System.out.println("");
        System.out.println("Ebay Kleinanzeigen Automator");
        System.out.println("----------------------------");
    }

    private void printSessionIdentifier()
    {
    	if(Context.get().isValidSession())
    	{
    		System.out.println("");
    		System.out.println("Using session identifier: " + Context.get().getSessionIdentifier());
    	}
    }

    private void printMainMenu()
    {
        printTitle();
        printSessionIdentifier();

        System.out.println("");
        System.out.println("1) Set account credentials");
        System.out.println("2) Export all existing small ads from the site to the local directory ("+outputPath+")");
        System.out.println("3) Activate all small ads at the site");
        System.out.println("4) Deactivate all small ads at the site");
        System.out.println("5) Delete inactive small ads at the site");
        System.out.println("6) Delete active small ads at the site");
        System.out.println("7) Import active small ads from your harddisk to the site");
        System.out.println("8) Import active and not yet existing small ads from your harddisk to the site");
        System.out.println("9) Import all small ads from your harddisk to the site");
        System.out.println("A) Query all existing small ads at the site");
        System.out.println("B) Set session identifier");
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

    private boolean handleSessionIdentifierReset(String sessionIdentifier)
    {
        if(!StringUtils.isBlank(sessionIdentifier) && Files.exists(new File(Context.get().getWorkingFilePath(sessionIdentifier)).toPath()))
        {
            Context.get().resetSessionIdentifier(sessionIdentifier);

        	return true;
        }
        else
        {
        	System.out.println("\nDirectory for session identifier '" + sessionIdentifier + "' was not found, is invalid or did not contain a data file. Make sure your data is located at ./data/" + sessionIdentifier + ".");

        	if(Context.get().isValidSession())
        	{
        		System.out.println("Keeping previous session identifier.");
        	}

        	return false;
        }
    }

    private boolean handleReadCredentials()
    {
    	System.out.println("\nSetting and verifying credentials\n");

        System.out.println("The following inputs will not be saved to harddisk.\n");

        System.out.print("Please enter your username (email) for ebay-kleinanzeigen.de and press enter: ");
        String username = readInput(false).trim();

        System.out.print("Please enter your password for ebay-kleinanzeigen.de and press enter: ");
        String password = readInput(true);

        System.out.println("");

        // Set the credentials to our account
        Context.get().setAccount(username, password);

        // Verify the account by trying to login
        return new LoginLogoutFlow().run();
    }

    public void intepretMainMenuInput(String input)
    {
        switch (input.toLowerCase())
        {
            case "1":
            {
                if(!handleReadCredentials())
                {
                	return;
                }
            }
            break;
            case "2":
            {
                System.out.println("\nExporting small ads: "+outputPath+"\n");

                // Always (re)set the session identifier on each new export
                // Avoids overwriting existing data and ensures that we always work on newest data export
                Context.get().resetSessionIdentifier(null);

                ExportSmallAdContainerFlow exportFlow = new ExportSmallAdContainerFlow();
                if (!exportFlow.run())
                {
                    return;
                }

                if (!exportFlow.getSmallAdContainer().writeToDisk(outputPath))
                {
                    return;
                }
            }
            break;
            case "3":
            {
                System.out.println("\nActivating small ads\n");

                if (!new ChangeSmallAdsStatusFlow().run(true))
                {
                    return;
                }
            }
            break;
            case "4":
            {
                System.out.println("\nDeactivating small ads\n");

                if (!new ChangeSmallAdsStatusFlow().run(false))
                {
                    return;
                }
            }
            break;
            case "5":
            {
                System.out.println("\nDeleting inactive small ads\n");

                if (!new DeleteSmallAdsFlow().run(false))
                {
                    return;
                }
            }
            break;
            case "6":
            {
                System.out.println("\nDeleting active small ads\n");

                if (!new DeleteSmallAdsFlow().run(true))
                {
                    return;
                }
            }
            break;
            case "7":
            {
                System.out.println("\nImporting active small ads\n");

                if(!Context.get().isValidSession())
                {
                	System.out.println("Nothing to import. Be sure to export first.");
                	return;
                }

                SmallAdContainer smallAdContainer = new SmallAdContainer();
                if (!smallAdContainer.readFromDisk(outputPath))
                {
                    return;
                }

                if (!new ImportSmallAdContainerFlow(smallAdContainer).run(true, false))
                {
                    return;
                }
            }
            break;
            case "8":
            {
                System.out.println("\nImporting active and not yet existing small ads\n");

                if(!Context.get().isValidSession())
                {
                	System.out.println("Nothing to import. Be sure to export first.");
                	return;
                }

                SmallAdContainer smallAdContainer = new SmallAdContainer();
                if (!smallAdContainer.readFromDisk(outputPath))
                {
                    return;
                }

                if (!new ImportSmallAdContainerFlow(smallAdContainer).run(true, true))
                {
                    return;
                }
            }
            break;
            case "9":
            {
                System.out.println("\nImporting all small ads\n");

                if(!Context.get().isValidSession())
                {
                	System.out.println("Nothing to import. Be sure to export first.");
                	return;
                }

                SmallAdContainer smallAdContainer = new SmallAdContainer();
                if (!smallAdContainer.readFromDisk(outputPath))
                {
                    return;
                }

                if (!new ImportSmallAdContainerFlow(smallAdContainer).run(false, false))
                {
                    return;
                }
            }
            break;
            case "a":
            {
            	System.out.println("\nQuerying all small ads\n");

            	QuerySmallAdsFlow queryFlow = new QuerySmallAdsFlow();
                if (!queryFlow.run())
                {
                    return;
                }
            }
            break;
            case "b":
            {
            	System.out.print("\nPlease enter the session identifier: ");

            	if(!handleSessionIdentifierReset(readInput(false)))
            	{
            		return;
            	}
            }
            break;
            case "0":
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

    public void runMainLoop()
    {
        do
        {
        	printMainMenu();
        	intepretMainMenuInput(readInput(false));
        }
        while (!exit);
    }

    public static void main(String[] args)
    {
    	// Create the application
    	App app = new App();

    	// Print the title if we do not go directly to the main loop
    	if(!Context.get().getConfiguration().credentialsFromConfiguration() || args.length > 0)
    	{
    		app.printTitle();
    	}

    	// Interpret command line parameters
        for(int i=0; i<args.length; i++)
        {
            app.processCommandLineParameter(args[i]);
        }

        // Read initial credentials
        if(!Context.get().getConfiguration().credentialsFromConfiguration())
        {
        	app.intepretMainMenuInput("1");
        }

        // Execute main loop
        app.runMainLoop();
    }
}