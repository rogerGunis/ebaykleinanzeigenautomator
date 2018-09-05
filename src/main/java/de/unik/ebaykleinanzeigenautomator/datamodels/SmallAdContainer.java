package de.unik.ebaykleinanzeigenautomator.datamodels;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import de.unik.ebaykleinanzeigenautomator.util.Context;

public class SmallAdContainer
{
    public String contact = "";

    public String sessionIdentifier = "";

    public List<SmallAd> smallAds = new ArrayList<SmallAd>();

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
                throw new ParseException("Invalid small ad '" + identifier + "' found", i);
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
            System.out.println("Failed to read file '" + inputFilePath + "'");
            System.out.println("Error was: " + e.getMessage());

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
        catch (Exception e)
        {
            System.out.println("Failed to interpret file '" + inputFilePath + "'");
            System.out.println("Error was: " + e.getMessage());

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
        Path outputFilePath = new File(outputPath).toPath();
        try
        {
            Files.createDirectories(outputFilePath.getParent());
        }
        catch (IOException e)
        {
            System.out.println("Failed to create directory '" + outputFilePath.getParent() + "'");
            System.out.println("Error was: " + e.getMessage());

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
            System.out.println("Failed to write file '" + outputFilePath + "'");
            System.out.println("Error was: " + e.getMessage());

            if (Context.get().getConfiguration().projectDebug())
            {
                e.printStackTrace();
            }

            return false;
        }

        return true;
    }
}
