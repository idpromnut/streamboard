package org.unrecoverable.streamboard;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.Data;

@Data
public class StreamBoardConfig {

	public static final double MIN_WINDOW_HEIGHT = 600;
	public static final double MIN_WINDOW_WIDTH = 800;
	
	private double locationX;
	private double locationY;
	private double width;
	private double height;
	@JsonIgnore
	private boolean newConfig = true;
	@JsonIgnore
	private boolean modified = false;
	@JsonIgnore
	private boolean windowUserModified = false;
	
	public StreamBoardConfig copy() {
		StreamBoardConfig copy = new StreamBoardConfig();
		copy.setLocationX(locationX);
		copy.setLocationY(locationY);
		copy.setWidth(width);
		copy.setHeight(height);
		copy.setModified(modified);
		copy.setNewConfig(newConfig);
		copy.setWindowUserModified(windowUserModified);
		return copy;
	}
	
	public boolean hasSaneWindowConfig(final Stage stage) {
		boolean stageIsVisibleOnScreen = false;
		final Rectangle2D stageRect = new Rectangle2D( stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight() );
		for(Screen screen: Screen.getScreens()) {
			if (screen.getBounds().contains( stageRect )) stageIsVisibleOnScreen = true;
		}
		return (stage.getHeight() >= MIN_WINDOW_HEIGHT && stage.getWidth() >= MIN_WINDOW_WIDTH && stageIsVisibleOnScreen);
	}
	
	public static StreamBoardConfig createEmpty() {
		StreamBoardConfig config = new StreamBoardConfig();
		return config;
	}
}
