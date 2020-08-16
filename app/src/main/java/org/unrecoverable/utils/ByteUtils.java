package org.unrecoverable.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import javax.xml.bind.DatatypeConverter;

/**
 *
 * Note: all bytes are written/read in network-byte-ordering.
 *
 * @author Chris Matthews
 */
public abstract class ByteUtils {

	/**
	 * Write an integer as an unsigned value in bytes to the provided OutputStream.
	 *
	 * @param iOut
	 * @param iValue
	 * @param iWidth
	 * @throws IOException
	 */
	public static void writeUnsignedInt(final OutputStream iOut, final int iValue, final int iWidth) throws IOException {
		writeUnsignedLong(iOut, iValue, iWidth);
	}

	/**
	 * Write a long as an unsigned value in bytes to the provided OutputStream.
	 *
	 * @param iOut
	 * @param iValue
	 * @param iWidth
	 * @throws IOException
	 */
	public static void writeUnsignedLong(final OutputStream iOut, final long iValue, final int iWidth) throws IOException {

		final long maxValueFromWidth = (long)Math.pow(2, iWidth*8) - 1;

		// check if iValue > 2^(iWdith * 8)
		if (iValue > maxValueFromWidth) {
			throw new IOException("cannot write " + iValue + " as a " + (iWidth*8) + " bit value (max value is " + maxValueFromWidth);
		}
		else {
			for(int i = iWidth - 1; i >= 0; i--) {
				iOut.write( (byte) ( ( iValue >> (i*8) ) & 0x00FF ) );
			}
		}
	}

	public static void writeSignedInt(OutputStream iOut, int iValue, int iWidth) throws IOException {
		writeUnsignedInt(iOut, iValue, iWidth);
	}


	public static int readUnsignedInt(InputStream iIn, int iWidth) throws IOException {
		int lValue;

		if (iWidth <= 0) {
			throw new IOException("width must be > 0");
		}

		lValue = (iIn.read() & 0x000000FF);
		for(int i = 1; i < iWidth; i++) {
			lValue = (lValue << 8) & 0xFFFFFF00;
			lValue |= iIn.read();
		}

		return lValue;
	}

	public static int readSignedInt(InputStream iIn, int iWidth ) throws IOException {
		int lValue = 0;
		int lTemp = 0;

		if (iWidth <= 0) {
			throw new IOException("width must be > 0");
		}

		lTemp = iIn.read();
		// is this a negative number?
		if ((byte)((byte)(lTemp >> 7) & 0x01) == 1) {
			lValue = ~lValue & 0xFFFFFF00;
		}

		lValue |= lTemp;

		for(int i = 1; i < iWidth; i++) {
			lValue = (lValue << 8) & 0xFFFFFF00;
			lValue |= iIn.read();
		}

		return lValue;
	}

	public static void appendableBuffer(final ByteBuffer iBuffer) {
		// move the position to the last valid byte
		iBuffer.position(iBuffer.limit());
		// move the limit to the end of the buffer
		iBuffer.limit(iBuffer.capacity());
	}

	public static String byteArrayToHexString(final byte[] iBytes) {
		return  DatatypeConverter.printHexBinary(iBytes);
	}

	public static byte[] hexStringToByteArray(final String iHexString) {
		return  DatatypeConverter.parseHexBinary(iHexString);
	}

	public static String printByteBuffer(final ByteBuffer iBuffer) {
		StringBuilder lBuilder = new StringBuilder("buffer{");
		lBuilder.append("position=").append(iBuffer.position()).append(", ");
		lBuilder.append("limit=").append(iBuffer.limit()).append(", ");
		lBuilder.append("capacity=").append(iBuffer.capacity()).append("}");
		return lBuilder.toString();
	}
}
