package de.unik.ebaykleinanzeigenautomator.datamodels;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class SmallAd
{
	public boolean isActive = true;
	
	public String location = "";
	
	public String creationDate = "";
	
	public String id = "";
	
	public String title = "";
	
	public String content = "";
	
	public String price = "";
	
	public boolean isFixedPrice = false;
	
	public boolean hasNoPrice = false;
	
	public boolean useContact = false;
	
	public List<String> images = new ArrayList<String>();
	
	public List<String> categories = new ArrayList<String>();
	
	public Hashtable<String, String> attributes = new Hashtable<String, String>(); 

	public boolean isValid()
	{
		boolean valid = !title.isEmpty() && !content.isEmpty() && (categories.size() >= 2);
		
		if(!hasNoPrice)
		{
			valid &= !price.isEmpty();
		}
		
		return valid;
	}
	
	public JSONObject toJson()
	{
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("isActive", isActive);
		jsonObject.put("location", location);
		jsonObject.put("creationDate", creationDate);
		jsonObject.put("id", id);
		jsonObject.put("title", title);
		jsonObject.put("content", content);
		jsonObject.put("price", price);
		jsonObject.put("isFixedPrice", isFixedPrice);
		jsonObject.put("hasNoPrice", hasNoPrice);
		jsonObject.put("useContact", useContact);
		
		final JSONArray imagesJsonArray = new JSONArray();
		images.forEach(i -> imagesJsonArray.put(i));
		jsonObject.put("images", imagesJsonArray);
		
		final JSONArray categoriesJsonArray = new JSONArray();
		categories.forEach(c -> categoriesJsonArray.put(c));
		jsonObject.put("categories", categoriesJsonArray);

		final JSONArray attributesJsonArray = new JSONArray();
		attributes.forEach((k, v) -> attributesJsonArray.put(new JSONObject().put(k, v)));
		jsonObject.put("attributes", attributesJsonArray);

		return jsonObject;
	}
	
	public String toString()
	{
		return toJson().toString(4);
	}
}
