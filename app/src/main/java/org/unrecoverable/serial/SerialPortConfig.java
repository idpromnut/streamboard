package org.unrecoverable.serial;

import lombok.Data;

@Data
public class SerialPortConfig {
	private String portName;
	private int baudRate;
	private int dataBits;
	private int stopBits;
	private int parity;
	
	public String prettyPrint() {
		return String.format("%s / %d %d-%s-%d", portName, baudRate, dataBits, ParityEnum.byId(parity).shortForm(),stopBits); 
	}
	
	public SerialPortConfig copy() {
		SerialPortConfig copy = new SerialPortConfig();
		copy.setPortName(this.portName);
		copy.setBaudRate(this.baudRate);
		copy.setDataBits(this.dataBits);
		copy.setStopBits(this.stopBits);
		copy.setParity(this.parity);
		return copy;
	}
}
