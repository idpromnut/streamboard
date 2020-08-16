package org.unrecoverable.utils;

import java.io.IOException;
import java.util.Arrays;

public class RingBuffer {

	private byte[] buffer;		// internal backing storage for this RingBuffer
	private int head;			// "write" pointer; this is the position that will be written to on next write()
	private int tail;			// "read" pointer, this is the position that will be read from on next read()
	private int capacity;		// the maximum number of bytes that can be written to this buffer before unread bytes are overwritten
	private int writeCount;
	private int readCount;

	public RingBuffer(int iCapacity) {
		if (iCapacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
		capacity = iCapacity;
		buffer = new byte[capacity];
		head = 0;
		tail = 0;
		writeCount = 0;
		readCount = 0;
	}

	public byte read() throws IOException {

		if ((writeCount - readCount) <= 0) {
			throw new IOException("no data");
		}

		byte lData = buffer[tail];
		tail++;
		readCount++;
		if (tail >= capacity) {
			tail = 0;
		}
		return lData;
	}

	public byte peek() throws IOException {

		if ((writeCount - readCount) <= 0) {
			throw new IOException("no data");
		}

		return buffer[tail];
	}

	public void write(byte iData) throws IOException {

		buffer[head] = iData;
		head++;
		writeCount++;
		if (head >= capacity) head = 0;

		if (available() > capacity) {
			read();
		}
	}

	public void clear() {
		head = 0;
		tail = 0;
		Arrays.fill(buffer, (byte)0x00);
		writeCount = 0;
		readCount = 0;
	}

	public int available() {
		return writeCount - readCount;
	}

	public int free() {
		return capacity - available();
	}
}
