package org.unrecoverable.streamboard.config;

import java.lang.reflect.Field;
import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SerialLibraryConfiguration {

	public static void configure() {
		// setup/configure library path for Serial IO library
		try {
			addLibraryPath("./lib/" + System.getProperty("os.arch"));
		} catch (Exception e) {
			log.error("could not configure library path", e);
		}
	}

	/**
	* Adds the specified path to the java library path
	*
	* @param pathToAdd the path to add
	* @throws Exception
	*/
	private static void addLibraryPath(String pathToAdd) throws Exception{
	    final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
	    usrPathsField.setAccessible(true);

	    //get array of paths
	    final String[] paths = (String[])usrPathsField.get(null);

	    //check if the path to add is already present
	    for(String path : paths) {
	        if(path.equals(pathToAdd)) {
	            return;
	        }
	    }

	    //add the new path
	    final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
	    newPaths[newPaths.length-1] = pathToAdd;
	    usrPathsField.set(null, newPaths);
	}
}
