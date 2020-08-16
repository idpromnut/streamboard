package org.unrecoverable.serial.events;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ByteDataEvent {

	@Getter
	private byte[] data;
	@Getter
	private Instant timestamp;
}
