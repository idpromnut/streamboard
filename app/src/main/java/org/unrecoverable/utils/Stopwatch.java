package org.unrecoverable.utils;

import java.time.Duration;
import java.time.LocalDateTime;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;

public class Stopwatch extends Task<Void> {

	private static final String DEFAULT_STOPPED_STRING = "--:--:--";
	
	private BooleanProperty stop = new SimpleBooleanProperty(true);
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;

	@Override
	protected Void call() throws Exception {
		startDateTime = LocalDateTime.now();
		reset();
		while (true) {

			if (!stop.getValue()) {
				endDateTime = LocalDateTime.now();
				Duration d = Duration.between(startDateTime, endDateTime);

				long hours = Math.max(0, d.toHours());
				long minutes = Math.max(0, d.toMinutes() - 60 * d.toHours());
				long seconds = Math.max(0, d.getSeconds() - 60 * d.toMinutes());

				updateMessage(String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
			}

			Thread.sleep(9);
		}
	}
	
	public void start() {
		startDateTime = LocalDateTime.now();
		stop.setValue(false);
	}
	
	public void end() {
		stop.setValue(true);
	}
	
	public void reset() {
		updateMessage(DEFAULT_STOPPED_STRING);
	}
}
