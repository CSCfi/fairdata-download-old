/**
 * 
 */
package fi.csc.fairdata.od;

import java.util.Deque;
import java.util.Map;

import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
//import io.undertow.util.Headers;
import io.undertow.server.HttpHandler;
/**
 * @author pj
 *
 */
public class MainServer {
	
	public final static String DATASET = "/api/v1/dataset/";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Undertow server = Undertow.builder()
                .addHttpListener(8180, "localhost")
                .setHandler(new HttpHandler() {
                    //@Override
                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
                    	Prosessor p = new Prosessor(exchange);
                    	String rp = exchange.getRequestPath();
                    	if (rp.startsWith("/api/v1/files")) {
                    		Map<String, Deque<String>> m = exchange.getQueryParameters();
                    		exchange.getResponseSender().send(p.files(m));
                    	} else if (rp.startsWith(DATASET)){                   		
                    		exchange.getResponseSender().send(p.dataset(rp.substring(DATASET.length())));
                    	} else { //ready for v2
                    		//exchange.setResponseCode(400);  //BadRequest .getResponseHeaders().
                    		exchange.setStatusCode(400);
                    		exchange.getResponseSender().send(exchange.getRequestPath()+
                    				" Not start with '/api/v1/files' or '"+DATASET+"'");
                    	}
                    }
                }).build();
        server.start();
	}

}
