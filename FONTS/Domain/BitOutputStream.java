package Domain;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public class BitOutputStream extends FilterOutputStream {
	
	class ControlBit {
		int  buff = 0;
		int  count = 0;

		/**
		 * If there is anything left to write
		 * @param next a bit as an Integer.
		 * @return Returns -1 if there is nothing yet to be written.
		 */
		private int  writeOne (int next) {
			int res = -1;
			buff = buff*2 + next;
			count++;
			if (count >= 7) {
				res = buff;
				count = 0;
				buff = 0;
			} 
			else res = -1;
			return res; 
		}

		/**
		 * Write the lasts bits to the output stream.
		 * @return the bits to write as an Integer.
		 */
		private int  writeLast () { 
			int x = 0;
			for (int i = 0; i < 7-count; ++i)
				x = x*2 + 1;
			for (int i = 7-count; i < 8; ++i)
				x = x*2;
			count = 0;
			int aux = buff;
			buff = 0;
			return aux|x; 
		}
	}

	ControlBit controlBit = new ControlBit();

	/**
	 * Constructor creates a new instance of BitOuputStream.
	 * @param os the output stream to write of.
	 */
	public BitOutputStream (OutputStream os) { 
		super(os); 
	}

	/**
	 * Writes a single bit into the included stream.
	 * @param i is a single bit given as an int.
	 * @throws IOException If there is a write problem.
	 */
	public void write1Bit (int i) throws IOException { 
		int x = controlBit.writeOne(i >= 1 ? 1:0);
		if (x >= 0) out.write(x); 
	}

	/**
	 * Writes a single bit into the included stream.
	 * @param i is a boolean to express a single bit, true: 1, false: 0
	 * @throws IOException If there is a write problem.
	 */
	public void write1Bit (boolean i) throws IOException { 
		int x = controlBit.writeOne(i ? 1:0);
		if (x >= 0) out.write(x); 
	}

	/**
	 * Writes a byte into the included stream.
	 * @param i the byte to write of.
	 * @throws IOException if writting to the output stream fails.
	 */
	public void write8Bit (int i) throws IOException {
		write1Bit((i >>> 7) & 0x00000001);
		write1Bit((i >>> 6) & 0x00000001);
		write1Bit((i >>> 5) & 0x00000001);
		write1Bit((i >>> 4) & 0x00000001);
		write1Bit((i >>> 3) & 0x00000001);
		write1Bit((i >>> 2) & 0x00000001);
		write1Bit((i >>> 1) & 0x00000001);
		write1Bit(i & 0x00000001);
	}

	/**
	 * Writes a byte into the included stream.
	 * @param i the byte to write of.
	 * @throws IOException if writting to the output stream fails.
	 */
	public void write8Bit (byte i) throws IOException {
		write8Bit((int)i);
	}

	/**
	 * Sets the EOF flag and flushes the stream.
	 * @throws IOException if writting to the output stream fails.
	 */
	public void eof() throws IOException {
		out.write(controlBit.writeLast());
		super.flush();
	}

	/**
	 * Flushes the stream of any element that may be or maybe not inside the stream.
	 */
	public void flush() throws IOException {
		if (controlBit.count > 0) 
			out.write(controlBit.writeLast());
		super.flush();
	}
}