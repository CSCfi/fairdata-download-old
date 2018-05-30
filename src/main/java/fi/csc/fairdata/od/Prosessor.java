/**
 * 
 */
package fi.csc.fairdata.od;
//import java.io.UnsupportedEncodingException;
//import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.Map;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

/**
 * @author pj
 *
 */
public class Prosessor {
	
	HttpServerExchange exchange;
	
	Prosessor(HttpServerExchange exchange) {
		this.exchange = exchange;
	}
		
	public String dataset(String rp, Map<String, Deque<String>> mp) {
		
		StringBuilder bb = new StringBuilder();
		exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
		String dsid =getDatasetID(rp);
		if (null != dsid) {
			bb.append(dsid + "\n");
			Deque<String> dsf = mp.get("file");
			if (null != dsf)
				dsf.forEach(e -> bb.append(e+ " "));
			Deque<String> dsd = mp.get("dir");
			bb.append("\n");
			if (null != dsd)
				dsd.forEach(e -> bb.append(e+" "));
		} else {
			this.exchange.setStatusCode(400);
			bb.append("datasetid on pakollinen parametri!!!");
		}
		return bb.toString();
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
