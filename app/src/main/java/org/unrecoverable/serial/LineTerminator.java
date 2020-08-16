package org.unrecoverable.serial;

import java.time.Duration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class LineTerminator {
	
	public static final String DEFAULT_CHAR_SEQUENCE = "\n";
	
	public enum Type {
		CHAR_SEQUENCE("Character Sequence", 0),
		TIMEOUT("Timeout", 1);
		
		@Getter
		private int value;
		
		private String description;
		
		private Type(String desc, int value) {
			this.description = desc;
			this.value = value;
		}
		
		public String toString() {
			return description;
		}
		
		public static Type byId(int id) {
			for(Type p: values()) {
				if (p.getValue() == id) return p;
			}
			return CHAR_SEQUENCE;
		}
	};

	private Type terminatorType;
	private String charSequence;
	private Duration timeout;
	
	public boolean isCharSequence() { return Type.CHAR_SEQUENCE.equals(terminatorType); }
	public boolean isTimeout() { return Type.TIMEOUT.equals(terminatorType); }

	public LineTerminator copy() {
		LineTerminator copy = new LineTerminator(this.terminatorType, this.charSequence, this.timeout);
		return copy;
	}
}
