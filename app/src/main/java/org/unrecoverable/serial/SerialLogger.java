package org.unrecoverable.serial;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Observer;
import java.util.Set;
import java.util.TooManyListenersException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import purejavacomm.CommPort;
import purejavacomm.CommPortIdentifier;
import purejavacomm.NoSuchPortException;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.SerialPortEvent;
import purejavacomm.SerialPortEventListener;
import purejavacomm.UnsupportedCommOperationException;

@Slf4j
public class SerialLogger implements SerialPortEventListener {

	private static final long[] TIMEOUT_BACKOFF_INTERVALS = {1000, 2000, 5000, 10000, 15000, 30000};

	private static final LineTerminator DEFAULT_LINE_TERMINATOR = new LineTerminator(LineTerminator.Type.CHAR_SEQUENCE, LineTerminator.DEFAULT_CHAR_SEQUENCE, null);
	
	@Setter @Getter
	private int bufferSize = 4096;

	@Getter @Setter
	private String name;

	private SerialPortConfig currentConfig = new SerialPortConfig();
	private CommPortIdentifier currentPortId = null;
	private SerialPort currentPort = null;
	private InputStream in;
	private boolean tryToReconnect = false;

	private ByteBufAllocator byteBufAllocator = UnpooledByteBufAllocator.DEFAULT;
	private ByteBuf rxBuffer;
	private final Set<Observer> newLogMessageObservers = new HashSet<>();
	private PortMonitorWorker portMonitorWorker = new PortMonitorWorker();

	@PostConstruct
	public void init() {
		rxBuffer = byteBufAllocator.buffer(bufferSize);
		portMonitorWorker.start();
	}

	public void destroy() {
		close();
		newLogMessageObservers.clear();
		portMonitorWorker.interrupt();
	}

	public void addObserver(Observer observer) {
		newLogMessageObservers.add(observer);
	}

	public void removeObserver(Observer observer) {
		newLogMessageObservers.remove(observer);
	}

	public void reconfigure(SerialPortConfig newConfig) {
		closeInternal();
		if (newConfig != null) {
			open(newConfig);
		}
		// otherwise we remove the current configuration
		else {
			currentConfig = null;
		}
	}

	public void open(SerialPortConfig newConfig) {

		if (newConfig == null) {
			throw new PortConfigurationException("no serial port configuration");
		} else if (!checkConfig(newConfig)) {
			throw new PortConfigurationException("invalid new configuration");
		} else if (portAlreadyOpen(currentPortId, newConfig)) {
			throw new PortConfigurationException("port is already open: " + newConfig.getPortName());
		} else {
			try {
				CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(newConfig.getPortName());
				CommPort newCommPort;
				SerialPort newSerialPort;

				if (portIdentifier.isCurrentlyOwned()) {
					throw new PortConfigurationException("port is currently in use: " + newConfig.getPortName());
				} else {
					newCommPort = portIdentifier.open(this.getClass().getName(), 2000);

					if (newCommPort instanceof SerialPort) {

						newSerialPort = (SerialPort) newCommPort;
						newSerialPort.setSerialPortParams(newConfig.getBaudRate(), newConfig.getDataBits(),
								newConfig.getStopBits(), newConfig.getParity());

						// update state with newly opened port information.
						currentConfig = newConfig;
						currentPortId = portIdentifier;
						currentPort = newSerialPort;
						in = newSerialPort.getInputStream();
						currentPort.addEventListener(this);
						currentPort.notifyOnDataAvailable(true);
						currentPort.notifyOnFramingError(true);
						currentPort.setDTR(true);
						currentPort.setRTS(true);
						tryToReconnect = true;
						log.debug("port {} opened", currentConfig.prettyPrint());
					} else {
						throw new PortConfigurationException("new config points to a non-serial port and only serial ports are supported");
					}
				}
			} catch (PortInUseException e) {
				throw new PortOpenException("port " + newConfig.getPortName() + " is already in use");
			} catch (NoSuchPortException e) {
				throw new PortOpenException("no such port " + newConfig.getPortName());
			} catch (UnsupportedCommOperationException e) {
				throw new PortOpenException("unable to open port " + newConfig.getPortName(), e);
			} catch (IOException e) {
				throw new PortOpenException("IO error while trying to open port " + newConfig.getPortName(), e);
			} catch (TooManyListenersException e) {
				throw new PortOpenException("too many listeners to port " + newConfig.getPortName(), e);
			}
		}
	}
	
	public void close() {
		tryToReconnect = false;
		closeInternal();
		log.debug("port {} closed", currentConfig.prettyPrint());
	}

	private void closeInternal() {
		if (currentPort != null && isPortOpen(currentPortId, currentConfig))  {
			currentPort.setDTR(false);
			currentPort.setRTS(false);
			currentPort.close();
		}
		
		currentPort = null;
		currentPortId = null;
		in = null;
	}

	public boolean isOpen() {
		if (currentConfig != null) {
			try {
				return isPortOpen(currentPortId, currentConfig);
			}
			catch (Exception e) {
				log.debug("could not check if port {} was open", currentConfig.prettyPrint(), e);
			}
			return false;
		}
		else {
			return false;
		}
	}
	


	@Override
	public void serialEvent(SerialPortEvent arg0) {
		int data;

		if (SerialPortEvent.DATA_AVAILABLE == arg0.getEventType()) {
			try {
				int bytesRead = 0;
				while ((in.available() > 0) && ((data = in.read()) > -1)) {
					if (!rxBuffer.isWritable()) {
						fireNewData();
					}
					else {
						rxBuffer.writeByte(data);
						bytesRead++;
					}
				}

				fireNewData();
				log.trace("read {} bytes, {} remaining on serial port", bytesRead, in.available());
			} catch (IOException e) {
				if (tryToReconnect == true) {
					log.error("Could not process data from serial port", e);
				}
				else {
					log.debug("Could not process data from serial port", e);
				}
			}
		}
		else {
			log.debug("serial port event: {}", printableSerialPortEventType(arg0.getEventType()));
		}
	}

	private void fireNewData() {
		if (rxBuffer.isReadable()) {
			String foundData = findTerminatedData(DEFAULT_LINE_TERMINATOR, rxBuffer);
			while(foundData != null)
			{
				if (foundData.length() > 0) {
					for(Observer o: newLogMessageObservers) {
						o.update(null, foundData);
					}
				}
				foundData = findTerminatedData(DEFAULT_LINE_TERMINATOR, rxBuffer);
			}
			rxBuffer.discardReadBytes();
		}
	}

	protected boolean isPortOpen(final CommPortIdentifier commPortId, final SerialPortConfig serialPortConfig) {
		if (commPortId != null && serialPortConfig != null) {
			try {
				CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(serialPortConfig.getPortName());

				if (commPortId.getName().equalsIgnoreCase(portIdentifier.getName())) {
					return true;
				}
			} catch (NoSuchPortException e) {
				// do nothing for now
			}
		}

		return false;
	}

	protected boolean checkConfig(final SerialPortConfig serialPortConfig) {

		if (StringUtils.isBlank(serialPortConfig.getPortName())) {
			return false;
		} else if (serialPortConfig.getBaudRate() <= 0) {
			return false;
		} else if (serialPortConfig.getDataBits() <= 0) {
			return false;
		} else if (serialPortConfig.getStopBits() < 0) {
			return false;
		} else if (serialPortConfig.getParity() < 0) {
			return false;
		}

		return true;
	}

	protected boolean portAlreadyOpen(final CommPortIdentifier commPortId, final SerialPortConfig serialPortConfig) {

		if (commPortId != null) {
			try {
				CommPortIdentifier lPortIdentifier = CommPortIdentifier.getPortIdentifier(serialPortConfig.getPortName());

				if (commPortId.getName().equalsIgnoreCase(lPortIdentifier.getName())) {
					return true;
				}
			} catch (NoSuchPortException e) {
				// do nothing for now
			}
		}

		return false;
	}

	protected String getPortTypeById(int portType) {
		switch (portType) {
		case CommPortIdentifier.PORT_PARALLEL:
			return "Parallel";
		case CommPortIdentifier.PORT_SERIAL:
			return "Serial";
		default:
			return "unknown type";
		}
	}
	
	private String findTerminatedData(LineTerminator terminatorConfig, ByteBuf buf) {

		String foundData = null;

		if (terminatorConfig.isCharSequence()) {
			byte[] terminatorSequence = terminatorConfig.getCharSequence().getBytes();
			int matchIndex = 0;
			int startMatchIndex = -1;
			for(int i = buf.readerIndex(); (i - buf.readerIndex()) < buf.readableBytes(); i++) {
				// found a different character than the termination sequence, reset and continue looking
				if ( startMatchIndex != -1 && buf.getByte(i) != terminatorSequence[matchIndex] ) {
					matchIndex = 0;
					startMatchIndex = -1;
				}
				else if (buf.getByte(i) == terminatorSequence[matchIndex]) {
					++matchIndex;

					if (startMatchIndex == -1) {
						startMatchIndex = i;
					}
					// we have a match
					else if (matchIndex == terminatorSequence.length) {
						int length = (i - buf.readerIndex()) + 1 - terminatorSequence.length;
						byte[] rawByteData = new byte[length];
						buf.readBytes(rawByteData, 0, length);
						foundData = new String(rawByteData, StandardCharsets.US_ASCII);
						// read the terminator sequence
						for(int j = 0; j < terminatorSequence.length; j++) buf.readByte(); // discard terminator bytes
						matchIndex = 0;
						startMatchIndex = -1;
						break;
					}
				}
			}
		}

		return foundData;
	}
	
	private static String printableSerialPortEventType(int serialEventType) {

		switch (serialEventType) {
			case SerialPortEvent.BI: return "BI";
			case SerialPortEvent.CD: return "CD";
			case SerialPortEvent.CTS: return "CTS";
			case SerialPortEvent.DATA_AVAILABLE: return "DATA";
			case SerialPortEvent.DSR: return "DSR";
			case SerialPortEvent.FE: return "FE";
			case SerialPortEvent.OE: return "OE";
			case SerialPortEvent.OUTPUT_BUFFER_EMPTY: return "OUTPUT_BUF";
			case SerialPortEvent.PE: return "PE";
			case SerialPortEvent.RI: return "RI";
			default: return "UNKNOWN";
		}
	}
	
	private class PortMonitorWorker extends Thread {
		@Override
		public void run() {
			boolean portOpen = false;
			int timeoutIdx = 0;

			while(true) {

				if (currentConfig != null) {
					if (currentPort != null && in != null) {
						portOpen = true;
						try {
							in.available();
							currentPort.isCD(); 
							currentPort.isDTR();
						}
						catch(Exception e) {
							portOpen = false;
							timeoutIdx = 0;
							log.warn("Console on port {} lost, will try to re-attach in {} seconds", 
									currentConfig.prettyPrint(), TimeUnit.SECONDS.convert(TIMEOUT_BACKOFF_INTERVALS[timeoutIdx], TimeUnit.MILLISECONDS));
							
						}
					}
					
					if (!portOpen && tryToReconnect) {
						// attempt to reconnect the serial port
						try {
							Thread.sleep(TIMEOUT_BACKOFF_INTERVALS[timeoutIdx]);
							reconfigure(currentConfig);
							portOpen = true;
							timeoutIdx = 0;
							log.info("Re-opened port {}", currentConfig.prettyPrint());
						}
						catch (Exception e) {
							if (timeoutIdx < TIMEOUT_BACKOFF_INTERVALS.length - 1) timeoutIdx++;
							log.trace("could not reopen port {}: {}", currentConfig.prettyPrint(), e.getMessage(), e);
							log.warn("Could not reopen port {}, will retry in {} seconds", 
									currentConfig.prettyPrint(), TimeUnit.SECONDS.convert(TIMEOUT_BACKOFF_INTERVALS[timeoutIdx], TimeUnit.MILLISECONDS));
						}
					}
				}

				try {
					Thread.sleep(1000);
				}
				catch(InterruptedException e) {
					return;
				}
			}
		}
	}
}
