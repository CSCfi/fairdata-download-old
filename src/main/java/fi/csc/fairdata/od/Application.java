/**
 * 
 */
package fi.csc.fairdata.od;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author pj
 *
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {
	
	  private static final String PROPERTIES = "/opt/secrets/metax.properties";
	  private static String auth;

	
	 @Override
	    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	        return application.sources(Application.class);
	    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
				
		SpringApplication.run(Application.class, args);
	}

	public static String getAuth() {
		return auth;
	}

	static {
	 Properties prop = new Properties();
     try {
         File f = new File(PROPERTIES);
         FileInputStream in = new FileInputStream(f);
         prop.load(in);

      auth= prop.getProperty("auth");
     }
     catch (IOException ex) {
         ex.printStackTrace();
     }	
	}
}
