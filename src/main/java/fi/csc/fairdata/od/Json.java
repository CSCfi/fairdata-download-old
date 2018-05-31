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
		JsonArray ja = jo.get("research_dataset").getAsJsonObject().get("files").getAsJsonArray();
		ja.forEach(o -> etsiidt(o));
		return ls;
	}

	private void etsiidt(JsonElement o) {
		
		if (null != o) {
			JsonElement i = o.getAsJsonObject().get("identifier");
			if (null != i)
				ls.add(i.getAsString());
		}
	}

}
