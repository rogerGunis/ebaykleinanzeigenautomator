package de.unik.ebaykleinanzeigenautomator.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class SmallAd
{
	public boolean isActive = true;
	
	public String creationDate = "";
	
	public String id = "";
	
	public String title = "";
	
	public String content = "";
	
	public String price = "";
	
	public boolean isFixedPrice = false;
	
	public boolean useContact = false;
	
	public List<String> images = new ArrayList<String>();
	
	public List<String> categories = new ArrayList<String>();
	
	public JSONObject toJson()
	{
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("isActive", isActive);
		jsonObject.put("creationDate", creationDate);
		jsonObject.put("id", id);
		jsonObject.put("title", title);
		jsonObject.put("content", content);
		jsonObject.put("price", price);
		jsonObject.put("isFixedPrice", isFixedPrice);
		jsonObject.put("useContact", useContact);
		
		final JSONArray imagesJsonArray = new JSONArray();
		images.forEach(i -> imagesJsonArray.put(i));
		jsonObject.put("images", imagesJsonArray);
		
		final JSONArray categoriesJsonArray = new JSONArray();
		categories.forEach(c -> categoriesJsonArray.put(c));
		jsonObject.put("categories", categoriesJsonArray);

		return jsonObject;
	}
	
	public String toString()
	{
		return toJson().toString(4);
	}
}
