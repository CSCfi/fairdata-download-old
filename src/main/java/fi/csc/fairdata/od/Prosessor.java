/**
 * 
 */
package fi.csc.fairdata.od;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
//import java.io.UnsupportedEncodingException;
//import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

/**
 * Pyynnön parameterit prosessoidaan sallitut tiedostot listaksi
 * 
 * @author pj
 *
 */
public class Prosessor {
	
	private static final String ZIP = ".zip";
	Dataset dataset;
	String auth;
	Metax m = null;
	Zip zip = null;
	Json json = null;
	final List<String> lf;
	
	Prosessor(Dataset ds, String files, String auth) {
        this.dataset = ds;
		this.auth = auth;
		if (null != files) {
			lf = Arrays.asList(files.split(","));//.stream().collect(Collectors.toList());
		} else
			lf= null;
	}
	
	/**
	 * Tarkistaa aineiston=dataset avoimuuden ja siihen kuuluvat tiedostot
	 * 
	 * @return List<Tiedosto> mahdolliset aneiston avoimet tiedostot lista
	 */
	public List<Tiedosto> metaxtarkistus() {

		String dsid = dataset.getId(); 
		if (null != dsid) {		
			m = new Metax(dsid, auth);	
			String dsd = dataset.getDir();
			MetaxResponse vastaus = m.dataset(dsid);
			if (vastaus.getCode() == 404) {
				virheilmoitus(404, "datasetid: "+dsid+ " Not found from metax.\n");
				return null;
			}			
			json = new Json();
			Vector<List<String>> v = json.file(vastaus.getContent());
			if (null != v) {
				dataset.setMetadata(vastaus.getContent()); // lisätään zippiin
				List<String> dsfiles = v.firstElement();
				List<String> dsdirs = v.lastElement();
				List<Tiedosto> tl = new ArrayList<Tiedosto>();
				dsdirs.forEach(d -> selvitähakemistonsisältömetaxista(d, tl));	
				dsfiles.forEach(f -> selvitätiedostonnimimetaxista(f, tl));
				if (null != lf) {
					// oikeasti voi lisätä zippiin!!!
					return tl.stream().filter(t -> lf.contains(t.getIdentifier())).collect(Collectors.toList());
				} else { // koko aineisto
					return tl;
				}
			} else {
				virheilmoitus(400, "Metaxin palauttamien datasetin tietojen parsinta epännistui "+
			"(yleensä tämä tarkoittaa, että datasetissä ei ole pääsyoikeustietoja).");
				return null;
			}
		
			//return null; //tänne ei pitäsi koskaan päätyä
		} else {	
			virheilmoitus(400, "datasetid on pakollinen parametri!!!");
			return null;
		}
	}	

	
	private void selvitätiedostonnimimetaxista(String f, List<Tiedosto> tl) {
		String jsons = m.file(f);
		if (null != jsons) {
			String filename = json.name(jsons);
			if (!Json.OPENACCESFALSE.equals(filename))
				tl.add(new Tiedosto(filename, f));
			else 
				System.out.println("Tiedosto "+f+" ei ollut avoin");
		}
	}

	/**
	 * Selvittää REKURSIIVISESTI (käyttäen  recursive=true parametria) metaxisata
	 * hakemiston kaikki tiedostot.
	 * 
	 * @param dir String hakemiston tunniste
	 * @param filelist List<String> palautetaan tiedostojen tunnisteet
	 */
	public void selvitähakemistonsisältömetaxista(String dir,  List<Tiedosto> filelist) {
		MetaxResponse d = m.directories(dir);
		if (200 == d.getCode())
			filelist.addAll(json.dir(d.getContent(), this));
		else {
			System.err.println("Metax vastasi "+dir+"-hakemistokyselyyn muuta kuin 200: "+d.getCode()+d.getContent());
		}
	}

	
	/**
	 * Näyttää käyttäjälle virheilmoituksen
	 * 
	 * @param code int HTTP status code
	 * @param sisältö String seliseli
	 */
	public void virheilmoitus(int code, String sisältö) {
		HttpServletResponse r = dataset.getResponse();
		r.setContentType("text/plain;charset=UTF-8");
		r.setStatus(code);
	    try {
			r.getWriter().println(sisältö);
			 r.flushBuffer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
 /* tilapäisesti pois käytöstä
	private void dirprosess(String id) {
		MetaxResponse j = m.directories(id);
		if (j.getCode() != 200) {
			zip.entry(Metax.DIR+id, j.getCode() + j.getContent());
			System.err.println("Dir vastaus: "+j.getCode() + j.getContent());
		} else 
			zip.entry(Metax.DIR+id, j.getContent());
	}
*/
}
