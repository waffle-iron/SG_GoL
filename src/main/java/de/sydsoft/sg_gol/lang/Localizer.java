package de.sydsoft.sg_gol.lang;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.sydsoft.libst.io.TextIO;
import de.sydsoft.libst.util.Console;

/**
 * 
 * @author syd
 */
public abstract class Localizer {

	/**
     * 
     */
	protected static Map<String, String>	loc		= new HashMap<>();
	/**
     * 
     */
	public static String					lang	= System.getProperty("user.language");
	/** */
	protected static String					version	= "0.0.0";
	private static Logger logger = Logger.getLogger(Localizer.class.getName());

	static {
		try {
			logger.log(Level.FINE, "detected Language is:"+lang);
			Properties p = new Properties();
			System.out.println(Localizer.class.getResourceAsStream("/de/sydsoft/sg_gol/lang/" + lang + ".properties"));
			p.load(Localizer.class.getResourceAsStream("/de/sydsoft/sg_gol/lang/" + lang + ".properties"));
			for (final String name : p.stringPropertyNames())
				loc.put(name, p.getProperty(name));
			loc.put("version", version());
		} catch (IOException e) {
			logger.log(Level.SEVERE, "in Localizer.static{}",e.fillInStackTrace());
		}
	}

	public static String get(String key, Object... ref) {
		return String.format(loc.get(key), ref);
	}

	public static String get(String key) {
		return loc.get(key);
	}

	private static String version() throws IOException {
		Properties p = new Properties(); 
		p.load(Localizer.class.getResourceAsStream("/de/sydsoft/sg_gol/txt/PatchNotes.properties"));
		String[] names = new String[p.size()];
		names = p.stringPropertyNames().toArray(names);
		Arrays.sort(names);
		version = names[names.length-1];
		return version;
	}
}