package Domain;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

public class BitInputStream extends FilterInputStream {

	class BitManager {
		private int[] buff = new int[8];
		private int count = -1 ;

		/**
		 * If we are at the end of the stream.
		 * @return true if we are at the end of the stream, otherwise false.
		 */
		private boolean atTheEnd () { 
			return ((buff[7] == 1) && (count < 0)); 
		}

		/**
		 * Set the flag for the end of stream.
		 */
		private void setTheEnd () { 
			buff[7] = 1;
			count = -1;
		}

		/**
		 * If we need to read the next byte.
		 * @return true if we need the next byte, otherwise false.
		 */
		private boolean noMoreBuffer () { 
			return count < 0; 
		}

		/**
		 * Set the buffer
		 * @param next put the bits of the byte into the array.
		 */
		private void setNext (int next) { 
			for (count = 0; count < 8; ++count) {
				buff[count] = next % 2;
				next /= 2;
			}
			// if this was the last byte
			if (buff[7] == 1) {
				for (count = 7;count >= 0; count--)
				if (buff[count] == 0) break;
				count--;
			} 
			else count = 6;
		}

		/**
		 * Get the next bit.
		 * @return the next bit of the buffer.
		 */		
		private int getNext() {
			return buff[count--]; 
		}
	};

	BitManager bitManager = new BitManager();

	private byte[] tempBuf = null;
	private int tempBufPtr = 0;
	private int tempBufLen = 0;

	/**
	 * Constructor creates a new instance of BitOutputStream.
	 * @param is the input stream to read of.
	 */
	public BitInputStream (InputStream is) { 
		super(is); 
	}

	/**
	 * Reads the next byte from the stream.
	 * @return the value of the next byte as an Integer.
	 * @throws IOException If reading from the input streams fails.
	 */
	private int readNextByte () throws IOException { 
		int val = -1;
		if (tempBufPtr==tempBufLen) val = super.read();
		else {
			byte b = tempBuf[tempBufPtr++];
			if ((b & 0x80) > 0) val = ((int)(b & 0x7F))|0x80;
				else val = b;
		}
		return val;
	}

	/**
	 * Reads a single bit from the included stream.
	 * @return Returns either 1 or 0, and at the end of stream returns -1.
	 * @throws IOException If reading from the input streams fails.
	 */
	public int read1Bit() throws IOException {
		if (bitManager.atTheEnd()) return -1;
		if (bitManager.noMoreBuffer()) {
			int i = readNextByte();
			if (i < 0) bitManager.setTheEnd();
			else bitManager.setNext(i);
			return read1Bit(); // CHECK THIS 
		}
		return bitManager.getNext();
	}

	/**
	 * Reads a byte from the included stream.
	 * @return Returns either 1 or 0, and at the end of stream returns -1.
	 * @throws IOException If reading from the input streams fails.
	 */
	public int read8Bit() throws IOException {
		int next;
		int result = 0;
		for (int i = 7; i >= 0; --i) {
			if ((next = read1Bit()) < 0) return -1;
			result |= next << i; 
		}
		return result & 0xFF;
	}
}