/**
 * 
 */
package fi.csc.fairdata.od;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
//import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
//import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
 
/**
 * @author pj
 *
 */
public class Zip {
	
	ByteArrayOutputStream baos;
	ZipOutputStream zout;
	
	public Zip() {
		this.baos = new ByteArrayOutputStream();
		this.zout = new ZipOutputStream(baos);
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

	public ByteBuffer getFinal() {
		try {
			zout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//System.out.println("Zip debug2: "+baos.size());
		ByteBuffer bb = ByteBuffer.wrap(baos.toByteArray());
		//System.out.println("Zip debug: "+bb.array().length);
		return bb;
	}

}
