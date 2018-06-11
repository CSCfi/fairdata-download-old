/**
 * 
 */
package fi.csc.fairdata.od;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
//import java.io.UnsupportedEncodingException;
//import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

/**
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
	
	Prosessor(Dataset ds, String auth) {
        this.dataset = ds;
		this.auth = auth;
	}
	
	/**
	 * Tarkistaa aineiston=dataset avoimuuden ja siihen kuuluvat tiedostot
	 * 
	 * @return boolean false ei kopioda tiedostoa. true: haetaan ja kopioidaan tiedostot.
	 */
	public boolean metaxtarkistus() {

		List<String> lf = null;

		String dsid = dataset.getId(); 
		if (null != dsid) {			
			m = new Metax(dsid, auth);
			String dsf = dataset.getFile();
			/*if (null != dsf) {
				lf = dsf.stream().filter(f -> oikeudet(f)).collect(Collectors.toList());
			}*/
			String dsd = dataset.getDir();
			if (null != dsd) {
				String[] sa = dsd.split(",");
				Arrays.asList(sa).forEach(e -> dirprosess(e));
			}
			MetaxResponse vastaus = m.dataset(dsid);
			if (vastaus.getCode() == 404) {
				virheilmoitus(404, "datasetid: "+dsid+ "Not found.\n"+vastaus.getContent());
				return false;
			}
			HttpServletResponse r = dataset.getResponse();
			json = new Json();
			Vector<List<String>> v = json.file(vastaus.getContent());
			if (null != v) {
				List<String> dsfiles = v.firstElement();
				List<String> dsdirs = v.lastElement();
				dsdirs.forEach(d -> selvitähakemistonsisältömetaxista(d, dsfiles));
				zip = new Zip(r);
				zip.entry("tiedostot"+dsid, dsfiles.toString()); //oikeasti zipattaisiin sisällöt
			} else {
				virheilmoitus(400, "Metaxin palauttamien datasetin tietojen parsinta epännistui "+
			"(yleensä tämä tarkoittaa, että datasetissä ei ole pääsyoikeustietoja).");
				return false;
			}
			r.setContentType("application/octet-stream; charset=UTF-8");
			try {
				String dz = dsid+ZIP;
				r.addHeader("Content-Disposition", "attachment; filename="+dz
						+ "\"; filename*=UTF-8''" +URLEncoder.encode(dz, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				System.err.println("UTF-8 ei muka löydy!");
				e.printStackTrace();
			}				
			zip.entry(dsid, vastaus.getContent()); //datasetin metadata: pois oikeasta?
			zip.sendFinal();
			//return true;
			return false; //oikeasti true
		} else {	
			virheilmoitus(400, "datasetid on pakollinen parametri!!!");
			return false;
		}
	}	

	
	/**
	 * Selvittää REKURSIIVISESTI (käyttäen  recursive=true parametria) metaxisata
	 * hakemiston kaikki tiedostot.
	 * 
	 * @param dir String hakemiston tunniste
	 * @param filelist List<String> palautetaan tiedostojen tunnisteet
	 */
	public void selvitähakemistonsisältömetaxista(String dir,  List<String> filelist) {
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
	void virheilmoitus(int code, String sisältö) {
		HttpServletResponse r = dataset.getResponse();
		r.setContentType("text/plain");
		r.setStatus(code);
	    try {
			r.getWriter().println(sisältö);
			 r.flushBuffer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Tarkistaa metaxista onko tiedosto open_access
	 * 
	 * @param id String fileid pid:urn:1
	 * @return boolean true if open_access
	 */
	private boolean oikeudet(String id) {
		//String vastaus = m.file(id);
		//Json.file(vastaus);
		return m.file(id);
	}

	private void dirprosess(String id) {
		MetaxResponse j = m.directories(id);
		if (j.getCode() != 200) {
			zip.entry(Metax.DIR+id, j.getCode() + j.getContent());
			System.err.println("Dir vastaus: "+j.getCode() + j.getContent());
		} else 
			zip.entry(Metax.DIR+id, j.getContent());
	}

}
