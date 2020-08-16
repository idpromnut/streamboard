package org.unrecoverable.streamboard.config;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.unrecoverable.streamboard.App;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

@Component
public class PathAndLoggingConfig {

	private static final String ENV_HOME_DIRECTORY = "STREAMBOARD_HOME_DIR";
	private static final String SYSTEM_PROPERTY_HOME_DIRECTORY = "home.dir";
	private static final String SYSTEM_PROPERTY_USER_DIRECTORY = "user.dir";
	private static final String SPRING_CONFIG_FILENAME = "streamboard.yml";
	private static final String LOGBACK_CONFIG_FILENAME = "logback.xml";
	private static final String DEFAULT_SPRING_CONFIG_FILENAME = "config/" + SPRING_CONFIG_FILENAME;
	private static final String BASE_LOG_DIR_SYSTEM_PROPERTY = "BASE_LOG_DIR";

	private static Logger log;
	private static String springConfigFileName = DEFAULT_SPRING_CONFIG_FILENAME;
	private static File homeDirectory = new File(".");
	private static File userDirectory = new File(".");

	public static void configureSystem(String[] args) {

		OptionParser parser = new OptionParser();
		parser.accepts("config")
				.withRequiredArg().ofType(String.class);
		parser.allowsUnrecognizedOptions();
		OptionSet options = parser.parse(args);

		File newHomeDirectory = null;
		String homeDirSetLog = "default";
		if (System.getenv().containsKey(ENV_HOME_DIRECTORY)) {
			newHomeDirectory = new File(System.getenv().get(ENV_HOME_DIRECTORY));
			homeDirSetLog = String.format("environment variable %s", ENV_HOME_DIRECTORY); 
		}
		else if (System.getProperty(SYSTEM_PROPERTY_HOME_DIRECTORY) != null) {
			newHomeDirectory = new File(System.getProperty(SYSTEM_PROPERTY_HOME_DIRECTORY));
			homeDirSetLog = String.format("system property variable %s", SYSTEM_PROPERTY_HOME_DIRECTORY); 
		}
		
		if (newHomeDirectory != null) {
			if (!newHomeDirectory.isDirectory()) {
				log = LoggerFactory.getLogger(App.class);
				log.warn("used {} to configure home directory to {}, but this directory does not exist",
						homeDirSetLog,
						newHomeDirectory.getAbsolutePath());
				homeDirSetLog = "default";
			}
			else if (!checkDirWritable(newHomeDirectory)) {
				log = LoggerFactory.getLogger(App.class);
				log.warn("used {} to configure home directory to {} from {}, but this directory is not writable",
						homeDirSetLog,
						newHomeDirectory.getAbsolutePath());
				homeDirSetLog = "default";
			}
			else {
				homeDirectory = newHomeDirectory.getAbsoluteFile();
				String previousLogConf = (String)System.getProperties().put("logging.config", homeDirectory.getAbsolutePath() + File.separator + LOGBACK_CONFIG_FILENAME);
				System.getProperties().put(BASE_LOG_DIR_SYSTEM_PROPERTY, homeDirectory.getAbsolutePath());
				setCurrentDirectory(homeDirectory);
				log = LoggerFactory.getLogger(App.class);
				log.info("Previous log configuration was {}, current log configuration is {}", previousLogConf, System.getProperties().get("logging.config"));
			}
		}
		else {
			log = LoggerFactory.getLogger(App.class);
		}
		log.info("setting home directory via {} to {}", homeDirSetLog, homeDirectory);

		if (System.getProperty(SYSTEM_PROPERTY_USER_DIRECTORY) != null) {
			userDirectory = new File(System.getProperty(SYSTEM_PROPERTY_USER_DIRECTORY));
		}
		
		log.info("setting user directory to: {}", userDirectory);
		
		if (options.has("config")) {
			springConfigFileName = (String)options.valueOf("config");
		}
		else {
			File configFile = new File(homeDirectory, SPRING_CONFIG_FILENAME);
			if (configFile.exists()) {
				springConfigFileName = homeDirectory.getAbsolutePath() + File.separator + SPRING_CONFIG_FILENAME;
			}
		}
		log.info("using configuration file located at {}", springConfigFileName);

		
		File tmpDir = new File(homeDirectory, "tmp");
		if (!tmpDir.exists()) {
			if (!tmpDir.mkdir()) {
				log.error("cannot create tmp directory {}", tmpDir.getAbsolutePath());
				System.exit(1);
			}
		}
		else if (!tmpDir.isDirectory()) {
			log.error("a file with the name {} exists in {}", tmpDir.getName(), tmpDir.getParentFile().getAbsolutePath());
			System.exit(1);
		}

		log.info("set java.io.tmp to {}", tmpDir.getAbsolutePath());
		System.setProperty("java.io.tmpdir", tmpDir.getAbsolutePath());
		
		try {
			File.createTempFile("test", "tmpdir.tmp").delete();
		} catch (IOException e) {
			log.error("could not create test temp file in {}", tmpDir.getAbsolutePath(), e);
		}
		
		// clear out temp directory
		try {
			FileUtils.cleanDirectory(tmpDir);
		} catch (IOException e) {
			log.warn("could not clean up temp directory before starting: {}", tmpDir.getAbsolutePath(), e);
		}
		
		System.setProperty("spring.config.location", springConfigFileName);
		log.info("Configured directories, logging and spring configuration file");
	}
	
	@Bean(name="homeDirectory")
	public File homeDirectory() {
		return homeDirectory;
	}

	@Bean(name="userDirectory")
	public File userDirectory() {
		return userDirectory;
	}

	public static File getHomeDirectory() {
		return homeDirectory;
	}

	public static File getUserDirectory() {
		return userDirectory;
	}

	private static boolean checkDirWritable(final File dir) {
		try {
			File testFile = File.createTempFile("test", "tmpdir.tmp", dir);
			return testFile.delete();
		} catch (IOException e) {
			log.debug("directory {} is not writable", dir.getAbsolutePath(), e);
		}
		return false;
	}

	private static boolean setCurrentDirectory(File directory) {
		return System.setProperty("user.dir", directory.getAbsolutePath()) != null;
    }
}
