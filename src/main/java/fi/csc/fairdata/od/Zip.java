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

	public void entry(String id, String metadata) {
		
		try {
			zout.putNextEntry(new ZipEntry(id.trim()+".json")); //tilapäinen
			byte[] md = metadata.getBytes("UTF-8");
			zout.write(md);
			//System.out.println("Zip debug: "+md.length);
			zout.closeEntry(); 

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

}
