/**
 * 
 */
package fi.csc.fairdata.od;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
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
		Deque<String> dsf = m.get("file");
		if (null != dsf)
			dsf.forEach(e -> bb.append(e+ " "));
		Deque<String> dsd = m.get("dir");
		bb.append("\n");
		if (null != dsd)
			dsd.forEach(e -> bb.append(e+" "));
		return bb.toString();
	}

	public String dataset(String id) {
		
		exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
		return id;
	
	}
	/*
	private byte[] getUTF8bytes(String s) {
		try {
			return s.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new byte[0];
		}
	}*/
	
}
