/**
 * 
 */
package fi.csc.fairdata.od;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
//import java.io.UnsupportedEncodingException;
//import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

/**
 * @author pj
 *
 */
public class Prosessor {
	
	private static final String ZIP = ".zip";
	HttpServerExchange exchange;
	String auth;
	Metax m = null;
	Zip zip = null;
	
	Prosessor(HttpServerExchange exchange, String auth) {
		this.exchange = exchange;
		this.auth = auth;
	}
		
	public ByteBuffer dataset(String rp, Map<String, Deque<String>> mp) {
				
		List<String> lf = null;
	
		String dsid =getDatasetID(rp); 
		if (null != dsid) {
			m = new Metax(dsid, auth);
			Deque<String> dsf = mp.get("file");
			/*if (null != dsf) {
				lf = dsf.stream().filter(f -> oikeudet(f)).collect(Collectors.toList());
			}*/
			zip = new Zip();
			Deque<String> dsd = mp.get("dir");
			if (null != dsd)
				dsd.forEach(e -> dirprosess(e));
			String vastaus = m.dataset(dsid);
			exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/octet-stream; charset=UTF-8");
			try {
				String dz = dsid+ZIP;
				exchange.getResponseHeaders().put(Headers.CONTENT_DISPOSITION, "attachment;filename=\""+dz
						                        + "\"; filename*=UTF-8''" +URLEncoder.encode(dz, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				System.err.println("UTF-8 ei muka löydy!");
				e.printStackTrace();
			}
			Json json = new Json();
			List<String> files = json.file(vastaus);
			zip.entry(dsid, vastaus);
			return zip.getFinal();
		} else {	
			exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
			this.exchange.setStatusCode(400);
			byte[] ba = null;
			try {
				ba = "datasetid on pakollinen parametri!!!".getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				System.err.println("UTF-8 ei muka löydy!!");
				e.printStackTrace();
			}
			ByteBuffer bb = ByteBuffer.allocate(ba.length);
			bb.put(ba);
			return  bb;
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
		String j = m.directories(id);
		zip.entry(Metax.DIR+id, j);
	}
	
	private String getDatasetID(String rp) {
		String loppu =rp.substring(MainServer.DATASET.length());
		if (loppu.isEmpty() || loppu.equals("") || loppu.equals("?"))
			return null;
		int end = loppu.indexOf("?");
		if ( end < 1) 
			return loppu;
		else 
			return loppu.substring(0, end);
	}

}
