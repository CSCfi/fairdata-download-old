/**
 * 
 */
package fi.csc.fairdata.od;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
//import com.google.gson.JsonPrimitive;

/**
 * Kaikki json parsinta. Luotetaan googlen laatuun eli tehdään gson:lla
 * 
 * @author pj
 *
 */
public class Json {

	/**
	 * Selvittää datasetin tiedostot ja hakemistot metaxin vastauksesta
	 * 
	 * @param vastaus String metaxin dataset/{id} kyselyn vastaus
	 * @return Vector<List<String>>, jonka alkioina tiedostolista ja hakemistolista
	 */
	public Vector<List<String>> file(String vastaus) {

		List<String> lsf = new ArrayList<String>();
		List<String> lsd = new ArrayList<String>();

		Gson gson = new GsonBuilder().create();
		JsonObject jo = gson.fromJson(vastaus, JsonObject.class);
		try {
			JsonObject rd = jo.get("research_dataset").getAsJsonObject();
			try {
				JsonObject at = rd.get("access_rights").getAsJsonObject().get("access_type").getAsJsonObject();
				JsonObject pl = at.get("pref_label").getAsJsonObject();
				if (!pl.get("en").getAsString().equals("Open")) {
					System.err.println("Aineisto ei ollut avoin.");
					return null; 
				}
			} catch (java.lang.NullPointerException e) {
				System.err.println("Datasetistä ei löytynyt pääsyoikeustietoja");
				return null; 
			}
			try {
				JsonArray ja = rd.get("files").getAsJsonArray();
				ja.forEach(o -> etsiidt(o, lsf));
			} catch (java.lang.NullPointerException e) {
				System.out.println("Datasetistä ei löytynyt tiedostoja.");
			}
			try {
				JsonArray ja = rd.get("directories").getAsJsonArray();
				ja.forEach(o -> etsiidt(o, lsd));
			} catch (java.lang.NullPointerException e) {
				System.out.println("Datasetistä ei löytynyt hakemistoja.");
			}
			Vector<List<String>> v  = new Vector<List<String>>();
			v.add(lsf);
			v.add(lsd);
			return v;
		} catch (java.lang.NullPointerException e) {
			System.err.println("Muu virhe json parsinnassa");
			return null;
		}
	}

	/**
	 * Parsii tiedoston tunnisteen (pitäisikö parsia storage???, joita testidatassa näy)
	 * 
	 * @param o JsonElement tiedoston tiedot
	 * @param ls List<String>, lista johon tunniste lisätään
	 */
	private void etsiidt(JsonElement o, List<String> ls) {
		
		if (null != o) {
			JsonElement i = o.getAsJsonObject().get("identifier");
			if (null != i)
				ls.add(i.getAsString());
		}
	}

	/**
	 * selvittää hakemiston tiedostot
	 * 
	 * @param content String metaxin vastaus hakemistokyselyyn
	 * @return List<String> tiedostolista
	 */
	public List<String> dir(String content, Prosessor p) {
		List<String> lsf = new ArrayList<String>();
		//List<String> alihakemistot = new ArrayList<String>();
		Gson gson = new GsonBuilder().create();
		JsonArray jo = gson.fromJson(content, JsonArray.class);
		if (null != jo) {
			jo.forEach(f -> etsistorageid(f, lsf));		
		}
		return lsf;
	}
	
	/**
	 * Parsii tiedoston storagetunnisteen
	 * 
	 * @param o JsonElement tiedoston tiedot
	 * @param ls List<String>, lista johon tunniste lisätään
	 */
	private void etsistorageid(JsonElement o, List<String> ls) {
		
		if (null != o) {
			JsonObject i = o.getAsJsonObject().get("file_storage").getAsJsonObject();
			if (null != i)
				ls.add(i.get("identifier").getAsString());
		}
	}

}
