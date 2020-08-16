package org.unrecoverable.utils;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TimestampUtils {

	private static DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss.SSS (O)");


	public static String instantToString(final Instant iTimestamp) {
		if (iTimestamp != null) {
			return timestampFormatter.format(iTimestamp.atZone(ZoneOffset.ofHours(0)));
		}
		else {
			return "N/A";
		}
	}


}
