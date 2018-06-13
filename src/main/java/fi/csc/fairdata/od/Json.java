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
				JsonElement id = at.get("identifier");
				if (!id.getAsString().contains("access_type/access_type_open_access")) {
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
	 * @return List<Tiedosto> tiedostolista
	 */
	public List<Tiedosto> dir(String content, Prosessor p) {
		List<Tiedosto> lsf = new ArrayList<Tiedosto>();
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
	private void etsistorageid(JsonElement o, List<Tiedosto> ls) {
		
		if (null != o) {
			JsonObject i = o.getAsJsonObject().get("file_storage").getAsJsonObject();
			if (null != i) {	
					try {
						String fp = o.getAsJsonObject().get("file_path").getAsString();
						String id = i.get("identifier").getAsString();
						ls.add(new Tiedosto(fp, id));
					} catch (java.lang.NullPointerException e) {
						System.err.println("Tiedoston tiedot vaillinaiset"+e.getMessage());
					}					
			} else
				System.err.println("file_storage puuttuu: "+o.getAsString());
		}
	}

}
