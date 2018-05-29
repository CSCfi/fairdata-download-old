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
	
	public String files(Map<String, Deque<String>> m ) {
		StringBuilder bb = new StringBuilder();
		exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
		Deque<String> ds = m.get("datasetid");
		if (null != ds) {
			bb.append(ds.getFirst() + "\n");
			Deque<String> dsf = m.get("file");
			if (null != dsf)
				dsf.forEach(e -> bb.append(e+ " "));
			Deque<String> dsd = m.get("dir");
			bb.append("\n");
			if (null != dsd)
				dsd.forEach(e -> bb.append(e+" "));
		} else {
			this.exchange.setStatusCode(400);
			bb.append("datasetid on pakollinen parametri!!!");
		}
		return bb.toString();
	}

	public String dataset(String id) {
		
		exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
		return id;
	
	}

}
