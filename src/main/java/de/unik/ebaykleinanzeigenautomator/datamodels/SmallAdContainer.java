package de.unik.ebaykleinanzeigenautomator.datamodels;

import de.unik.ebaykleinanzeigenautomator.util.Context;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class SmallAdContainer
{
    public String contact = "";

    public String sessionIdentifier = "";

    public List<de.unik.ebaykleinanzeigenautomator.datamodels.SmallAd> smallAds = new ArrayList<>();

    public SmallAdContainer()
    {
        this(Context.get().getSessionIdentifier());
    }

    public SmallAdContainer(String sessionIdentifier)
    {
        this.sessionIdentifier = sessionIdentifier;
    }

    public void fromJSON(JSONObject jsonObject) throws ParseException
    {
        JSONArray jsonArray = jsonObject.getJSONArray("smallAds");

        contact = jsonObject.getString("contact");
        sessionIdentifier = jsonObject.getString("sessionIdentifier");
        
        if(jsonArray.isEmpty())
        {
            throw new ParseException("File does not contain any small ads", 0);
        }

        for (int i = 0; i < jsonArray.length(); i++)
        {
            SmallAd smallAd = SmallAd.fromJSON(jsonArray.getJSONObject(i));

            if (smallAd.isValid())
            {
                smallAds.add(smallAd);
            }
            else
            {
                String identifier = !smallAd.title.isEmpty() ? smallAd.title : smallAd.id;
                throw new ParseException("Invalid small ad " + identifier + " found", i);
            }
        }
    }

    public JSONObject toJson()
    {
        JSONArray jsonArray = new JSONArray();
        smallAds.forEach(a -> jsonArray.put(a.toJson()));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("contact", contact);
        jsonObject.put("sessionIdentifier", sessionIdentifier);
        jsonObject.put("smallAds", jsonArray);

        return jsonObject;
    }

    public String toString()
    {
        return toJson().toString(4);
    }

    public boolean readFromDisk(String inputPath)
    {
        Path inputFilePath = new File(inputPath).toPath();
        String jsonString = null;
        try
        {
            jsonString = new String(Files.readAllBytes(inputFilePath), StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            System.out.println("Failed to read file from " + inputFilePath);
            System.out.println("Error was: " + e.toString());

            if (Context.get().getConfiguration().projectDebug())
            {
                e.printStackTrace();
            }

            return false;
        }

        try
        {
            fromJSON(new JSONObject(jsonString));
        }
        catch (ParseException e)
        {
            System.out.println("Failed to interpret file " + inputFilePath);
            System.out.println("Error was: " + e.toString());

            if (Context.get().getConfiguration().projectDebug())
            {
                e.printStackTrace();
            }

            return false;
        }

        return true;
    }

    public boolean writeToDisk(String outputPath)
    {
        if(smallAds.isEmpty())
        {
            System.out.println("Skipped writing small ads to disk");
            return true;
        }
        
        Path outputFilePath = new File(outputPath).toPath();
        try
        {
            Files.createDirectories(outputFilePath.getParent());
        }
        catch (IOException e)
        {
            System.out.println("Failed to create directory at " + outputFilePath.getParent());
            System.out.println("Error was: " + e.toString());

            if (Context.get().getConfiguration().projectDebug())
            {
                e.printStackTrace();
            }

            return false;
        }

        try
        {
            Files.write(outputFilePath, this.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
        catch (IOException e)
        {
            System.out.println("Failed to write file at " + outputFilePath);
            System.out.println("Error was: " + e.toString());

            if (Context.get().getConfiguration().projectDebug())
            {
                e.printStackTrace();
            }

            return false;
        }

        return true;
    }
}
