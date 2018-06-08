/**
 * 
 */
package fi.csc.fairdata.od;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * @author pj
 *
 */
public class Dataset {
	private final String id;
	private final String file; //comma separeted list
	private final String dir;
	private final HttpServletResponse response;
	
	public Dataset(String id, String file, String dir, HttpServletResponse response) {
		this.id = id;
		this.file = file;
		this.dir = dir;
		this.response = response;
	}

	public void copy() {
		Tiedostonkäsittely tk = new  Tiedostonkäsittely(response);
		tk.test(id);
	}

	public boolean check() {
		Prosessor p = new Prosessor(this, Application.getAuth());
		return p.metaxtarkistus();
	}
	
	
	public String getId() {
        return id;
    }
	
	public String getFile() {
        return file;
    }

	public String getDir() {
		return dir;
	}
	
	public HttpServletResponse getResponse() {
		return response;
	}
}
