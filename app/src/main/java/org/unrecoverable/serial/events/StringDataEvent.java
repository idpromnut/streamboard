package org.unrecoverable.serial.events;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class StringDataEvent {

	@Getter
	private String data;
	@Getter
	private Instant timestamp;
}
