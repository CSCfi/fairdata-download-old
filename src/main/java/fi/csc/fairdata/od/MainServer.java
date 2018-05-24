/**
 * 
 */
package fi.csc.fairdata.od;

import java.util.Deque;
import java.util.Map;

import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.server.HttpHandler;
/**
 * @author pj
 *
 */
public class MainServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Undertow server = Undertow.builder()
                .addHttpListener(8180, "localhost")
                .setHandler(new HttpHandler() {
                    //@Override
                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                    	if (exchange.getRequestPath().startsWith("/api/v1")) {
                    		Map<String, Deque<String>> m = exchange.getQueryParameters();
                    		exchange.getResponseSender().send("Hello "+ m.get("file"));
                    	} else {
                    		exchange.getResponseSender().send(exchange.getRequestPath());
                    	}
                    }
                }).build();
        server.start();
	}

}
