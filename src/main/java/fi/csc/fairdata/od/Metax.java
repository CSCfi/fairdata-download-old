/**
 * 
 */
package fi.csc.fairdata.od;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

/**
 * @author pj
 *
 */
public class Metax {

	private final static String METAXURL = "https://metax-test.csc.fi/rest/files/";
	String encoding = null;

	
	public Metax() {
		try {
			encoding = Base64.getEncoder().encodeToString(("fds:geFs5Vvyg7s78wAK").getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public boolean file(String id) {	
		//StringBuffer content = new StringBuffer();
		boolean b = false;
		HttpURLConnection con = null;
		try {
			URL url = new URL(METAXURL+id);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");			
			con.setRequestProperty  ("Authorization", "Basic " + encoding);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream(), "UTF-8"));//con.getContentEncoding()
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				if (inputLine.contains("open_access")) {
					System.out.println(inputLine);
					if (inputLine.contains("true")) 
						b = true;
				} 
				//content.append(inputLine);
			}
			in.close();
			con.disconnect(); //??
			return b;
		} catch (IOException e) { //https://docs.oracle.com/javase/7/docs/technotes/guides/net/http-keepalive.html
	        try {
	                int respCode = ((HttpURLConnection)con).getResponseCode();
	                InputStream es = ((HttpURLConnection)con).getErrorStream();
	                int ret = 0;
	                // read the response body
	                byte[] buf = new byte[8192];
	                while ((ret = es.read(buf)) > 0) {
	                	System.err.println("Virhetilanne: "+buf);
	                }
	                // close the errorstream
	                es.close();
	        } catch (IOException e2) {
	        	System.err.println(e2.getMessage());
	        }
		}
		return false;//content.toString();
	}

}
