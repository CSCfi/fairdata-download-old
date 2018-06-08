/**
 * 
 */
package fi.csc.fairdata.od;

import java.util.ArrayList;
import java.util.List;
//import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
//import com.google.gson.JsonPrimitive;

/**
 * @author pj
 *
 */
public class Json {
	
	List<String> ls = new ArrayList<String>();

	public List<String> file(String vastaus) {
		Gson gson = new GsonBuilder().create();
		JsonObject jo = gson.fromJson(vastaus, JsonObject.class);
		try {
			JsonObject rd = jo.get("research_dataset").getAsJsonObject();
			JsonObject at = rd.get("access_rights").getAsJsonObject().get("access_type").getAsJsonObject();
			JsonObject pl = at.get("pref_label").getAsJsonObject();
			if (!pl.get("en").getAsString().equals("Open")) {
				return null; //Aineisto ei ollut avoin
			}
			JsonArray ja = rd.get("files").getAsJsonArray();
			ja.forEach(o -> etsiidt(o));
			return ls;
		} catch (java.lang.NullPointerException e) {
			System.err.println("Datasetistä ei löytynyt tiedostoja.");
			return null;
		}
	}

	private void etsiidt(JsonElement o) {
		
		if (null != o) {
			JsonElement i = o.getAsJsonObject().get("identifier");
			if (null != i)
				ls.add(i.getAsString());
		}
	}

}
