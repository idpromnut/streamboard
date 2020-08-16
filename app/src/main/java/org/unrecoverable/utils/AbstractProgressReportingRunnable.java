package org.unrecoverable.utils;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleFloatProperty;

public abstract class AbstractProgressReportingRunnable implements ProgressReportingRunnable {

	private Property<Number> progress = new SimpleFloatProperty(-1.0f);
	private boolean running = true;

	@Override
	public Property<Number> getProgressProperty() {
		return progress;
	}

	@Override
	public void setProgressProperty(Property<Number> iProgressProperty) {
		progress = iProgressProperty;
	}


	@Override
	public void setProgress(Number iProgress) {
		progress.setValue(iProgress);
	}

	@Override
	public void terminate() {
		running = false;
	}

	@Override
	public boolean isRunning() {
		return running;
	}
}
