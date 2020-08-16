package org.unrecoverable.utils;

import javafx.beans.property.Property;

public interface ProgressReportingRunnable extends Runnable {

	/**
	 * Gets the progress property for this runnable.
	 *
	 * @return
	 */
	Property<Number> getProgressProperty();

	/**
	 * Sets the progress property for this runnable.
	 *
	 * Note that if this value is set to -1.0, this means that the current progress is
	 * not known.
	 *
	 * @param iProgressProperty
	 */
	void setProgressProperty(final Property<Number> iProgressProperty);

	void setProgress(final Number iProgress);

	/**
	 * Instructs this runnable to terminate as soon as possible.
	 */
	void terminate();

	boolean isRunning();
}
