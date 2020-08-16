package org.unrecoverable.streamboard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.prefs.Preferences;

import org.apache.commons.lang3.StringUtils;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Gui extends Application {

//	private final SpringFxmlLoader loaderFactory = new SpringFxmlLoader();
	private final Preferences prefs = Preferences.userNodeForPackage(App.class);

	private static AtomicReference<Gui> singleton = new AtomicReference<>();
	private static final AtomicInteger DEFAULT_NAME_NUMBER = new AtomicInteger(0);
	private static final String DEFAULT_NAME_TEMPLATE = "Serial Slurp - %s";
	private static final String DEFAULT_NAME_TEMPLATE_UNTITLED = "untitled_%d";
	
	public static final void run(String[] args) {
		launch(args);
	}

	public Gui() {
		super();
		singleton.set(this);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		// see if there are any "active" console configurations that should be loaded up at startup
		List<File> foundConfigFiles = loadConfigFilesFromPrefs();
		if (!foundConfigFiles.isEmpty()) {
			for(File configFile: foundConfigFiles) {
				log.debug("Loading console configuration from {}", configFile);
				createNewStreamBoardWindow(configFile);
			}
		}
		else {
			// start a new console window
			createNewStreamBoardWindow(null);
		}
	}

	public static Gui getGui() {
		return singleton.get();
	}
	
	public void createNewStreamBoardWindow(final File configFile) {
	}
	
	public void quit() {
	}

	public void setWindowTitle(final File configFile, Stage stage) {
		if (configFile != null) {
			stage.setTitle(String.format(DEFAULT_NAME_TEMPLATE, configFile.getAbsolutePath()));
		}
		else {
			stage.setTitle(String.format(DEFAULT_NAME_TEMPLATE, String.format(DEFAULT_NAME_TEMPLATE_UNTITLED, DEFAULT_NAME_NUMBER.get())));
		}
	}

	private List<File> loadConfigFilesFromPrefs() {
		List<File> foundConfigFiles = new ArrayList<>();
		for(String configFilename: StringUtils.split(prefs.get(App.PREF_OPEN_CONSOLE_LIST, ""), ",")) {
			File configFile = new File(configFilename);
			if (configFile.exists() && !configFile.isDirectory()) foundConfigFiles.add(configFile);
		}
		return foundConfigFiles;
	}
}
