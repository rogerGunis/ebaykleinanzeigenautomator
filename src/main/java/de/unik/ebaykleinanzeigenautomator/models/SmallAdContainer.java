package de.unik.ebaykleinanzeigenautomator.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class SmallAdContainer
{
	public String contact = "";
	
	public List<SmallAd> smallAdds = new ArrayList<SmallAd>();
	
	public JSONObject toJson()
	{
		JSONArray jsonArray = new JSONArray();
		smallAdds.forEach(a -> jsonArray.put(a.toJson()));
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("contact", contact);
		jsonObject.put("smallAds", jsonArray);
		
		return jsonObject;
	}
	
	public String toString()
	{
		return toJson().toString(4);
	}
}
