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
	
	Prosessor(Dataset ds, String auth) {
        this.dataset = ds;
		this.auth = auth;
	}
		
	public boolean metaxtarkistus() {

		List<String> lf = null;

		String dsid = dataset.getId(); 
		if (null != dsid) {			
			m = new Metax(dsid, auth);
			String dsf = dataset.getFile();
			/*if (null != dsf) {
				lf = dsf.stream().filter(f -> oikeudet(f)).collect(Collectors.toList());
			}*/
			zip = new Zip(dataset.getResponse());
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
			r.setContentType("application/octet-stream; charset=UTF-8");
			try {
				String dz = dsid+ZIP;
				r.addHeader("Content-Disposition", "attachment; filename="+dz
						+ "\"; filename*=UTF-8''" +URLEncoder.encode(dz, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				System.err.println("UTF-8 ei muka löydy!");
				e.printStackTrace();
			}
			
			Json json = new Json();
			List<String> files = json.file(vastaus.getContent());
			zip.entry(dsid, vastaus.getContent());
			zip.sendFinal();
			//return true;
			return false; //oikeasti true
		} else {	
			virheilmoitus(400, "datasetid on pakollinen parametri!!!");
			return false;
		}
	}	

	

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
