/**
 * 
 */
package fi.csc.fairdata.od;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
//import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
//import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;
 
/**
 * @author pj
 *
 */
public class Zip {
	
	ByteArrayOutputStream baos;
	ZipOutputStream zout;
	HttpServletResponse response;
	
	public Zip(HttpServletResponse r) {
		
		try {
			this.zout = new ZipOutputStream(r.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.response = r;
	}

	public void entry(String path) {
		
		try {
			zout.putNextEntry(new ZipEntry(path.trim()));
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	

	public void sendFinal() {
		try {
			zout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	ZipOutputStream getZout() {
		return zout;
	}
}
