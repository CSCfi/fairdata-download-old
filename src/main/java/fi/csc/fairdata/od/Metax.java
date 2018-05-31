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

	private final static String METAXREST = "https://metax-test.csc.fi/rest/";
	private final static String METAXDATASETURL = METAXREST+"datasets/";
	private final static String METAXFILEURL = METAXREST+"files/";
	private final static String FORMAT = "?format=json";
	String encoding = null;

	
	public Metax() {
		try {
			encoding = Base64.getEncoder().encodeToString(("placeholder").getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public String dataset(String id) {
		StringBuffer content = new StringBuffer();
		HttpURLConnection con = null;
		try {
			URL url = new URL(METAXDATASETURL+id+FORMAT);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");			
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream(), "UTF-8"));//con.getContentEncoding()
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();
			con.disconnect(); //??

		} catch (IOException e2) {
			try {
			int respCode = ((HttpURLConnection)con).getResponseCode();
			InputStream es = ((HttpURLConnection)con).getErrorStream();
			int ret = 0;
			byte[] buf = new byte[8192];
            while ((ret = es.read(buf)) > 0) {
            	System.err.print("Dataset virhetilanne "+respCode+": ");
            	System.err.write(buf);
            	System.err.println();
            }
            es.close();
	        } catch (IOException e3) {
	        	System.err.println(e3.getMessage());
	        }
			System.err.println(e2.getMessage());
		}

		return content.toString();
	}
	
	
	public boolean file(String id) {	
		boolean b = false;
		HttpURLConnection con = null;
		try {
			URL url = new URL(METAXFILEURL+id+FORMAT);
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
	                	System.err.println("File virhetilanne "+respCode+": "+buf.toString());
	                	
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
