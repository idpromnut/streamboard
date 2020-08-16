package org.unrecoverable.serial;

import lombok.Getter;

public enum ParityEnum {
	NONE("None", 0),
	ODD("Odd", 1),
	EVEN("Even", 2);

	@Getter
	private int value;
	
	private String description;
	
	private ParityEnum(String desc, int value) {
		this.description = desc;
		this.value = value;
	}
	
	public String toString() {
		return description;
	}
	
	public String shortForm() {
		return description.substring(0,1);
	}
	
	public static ParityEnum byId(int id) {
		for(ParityEnum p: values()) {
			if (p.getValue() == id) return p;
		}
		
		return NONE;
	}
}
