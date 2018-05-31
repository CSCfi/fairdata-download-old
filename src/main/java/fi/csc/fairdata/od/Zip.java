/**
 * 
 */
package fi.csc.fairdata.od;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
 
/**
 * @author pj
 *
 */
public class Zip {

	public ByteBuffer string(String id, String metadata) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zout = new ZipOutputStream(baos);
		try {
			zout.putNextEntry(new ZipEntry(id));
			byte[] md = metadata.getBytes("UTF-8");
			zout.write(md);
			System.out.println("Zip debug: "+md.length);
			zout.closeEntry(); 
			
			zout.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//ByteBuffer bb = ByteBuffer.allocate(baos.size());
		System.out.println("Zip debug2: "+baos.size());
		ByteBuffer bb = ByteBuffer.wrap(baos.toByteArray());
		System.out.println("Zip debug: "+bb.array().length);
		return bb;
	}

}
