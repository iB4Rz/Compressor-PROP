package Domain;

public class ByteArray {
    final private byte[] array;

    /**
     * Default constructor, 0 length array.
     */
    public ByteArray() {
        array = new byte[0];
    }

    /**
     * Constructor with a single byte.
     * @param b the byte to be contained in the new byte array.
     */
    public ByteArray(byte b) {
        array = new byte[] { b };
    }

    /**
     * Cloning constructor.
     * @param ba the byte array to be cloned.
     */
    public ByteArray(byte[] ba) {
        array = (byte[]) ba.clone();
    }

    /**
     * Returns the size of the byte array.
     * @return the size as a Integer.
     */
    public int size() {
        return array.length;
    }

    /**
     * Compares the array of an object with the main byte array. Need it for the hash-table.
     * @return true if the two arrays are equal, otherwise false.
     */
    public boolean equals(Object o) {
        ByteArray ba = (ByteArray) o;
        return java.util.Arrays.equals(array, ba.array);
    }

    /**
     * Hash code for the hash-table.
     * @return the code of the hash-table.
     */
    public int hashCode() {
        int code = 0;
        for (int i = 0; i < array.length; ++i)
            code = code * 2 + array[i];
        return code;
    }

    /**
     * Returns the byte in a given position.
     * @param p position to get the byte of.
     * @return the byte in the position p.
     */
    public byte getBytePos(int p) {
        return array[p];
    }

    /**
     * Concatenates another byte array into this one.
     * @param ba the ByteArray to concatenate.
     * @return the concatenation in another newly created one.
     */
    public ByteArray concatenate(ByteArray ba) {
        int n = size() + ba.size();
        byte[] b = new byte[n];
        for (int i = 0; i < size(); ++i)
            b[i] = getBytePos(i);
        for (int i = 0; i < ba.size(); ++i)
            b[i + size()] = ba.getBytePos(i);
        return new ByteArray(b);
    }

    /**
     * Concatenates a byte in this array of bytes.
     * @param b the byte to concatenate.
     * @return the concatenations in another newly created one.
     */
    public ByteArray concatenate(byte b) {
        return concatenate(new ByteArray(b));
    }

    /**
     * Returns the ByteArray between [beginIndex, endIndex].
     * @param beginIndex the first desired index.
     * @param endIndex the final desired index.
     * @return the ByteArray between [beginIndex, endIndex].
     */
    public ByteArray subByteArray(int beginIndex, int endIndex) {
        assert (beginIndex >= 0);
        assert (endIndex >= beginIndex);
        assert (array.length >= endIndex);
        byte[] bar = new byte[endIndex - beginIndex];
        for (int i = 0; i < bar.length; ++i)
            bar[i] = array[beginIndex + i];
        return new ByteArray(bar);
    }

    
    /**
     * Returns the index of the first ocurrence of ByteArray ba in this ByteArray.
     * @param ba the ByteArray to look for.
     * @return the index, otherwise returns -1.
     */
    public int indexOf(ByteArray ba) {
        if (ba.size() == 0)
            return -1;
        int end = (array.length - ba.size()) + 1;
        for (int i = 0; i < end; ++i)
            for (int j = 0; j < ba.size(); ++j) {
                if (ba.getBytePos(j) != array[i + j])
                    break;
                else if (j == (ba.size() - 1))
                    return i;
            }
        return -1;
    }

    /**
     * Returns a ByteArray without the compressed values between [beginIndex, endIndex).
     * @param beginIndex the first desired index.
     * @param endIndex the final desired index.
     * @return a ByteArray that has eliminated the values between [beginIndex, endIndex).
     */
    public ByteArray delete(int beginIndex, int endIndex) {
        assert (beginIndex >= 0);
        assert (endIndex >= 0);
        endIndex = Math.min(endIndex, array.length);
        if (beginIndex == endIndex) return this;
        byte[] aux = new byte[array.length - (endIndex-beginIndex)];
        System.arraycopy(array, 0, aux, 0, beginIndex);
        System.arraycopy(array, endIndex, aux, beginIndex, array.length - endIndex);
        return new ByteArray(aux);
    }

    /**
     * Returns a byte array of the copy of the inner byte arrays.
     * @return a byte array.
     */
    public byte[] getBytes() {
        return (byte[]) array.clone();
    }

    /**
     * Drops the last byte.
     * @return the last byte of the ByteArray.
     */
    public byte getLastByte() {
        return array[size() - 1];
    }

    /**
     * Returns the ByteArray with the last byte drop.
     * @return the ByteArray with the last byte drop.
     */
    public ByteArray dropLast() {
        byte[] arr = new byte[size() - 1];
        for (int i = 0; i < arr.length; ++i)
            arr[i] = array[i];
        return new ByteArray(arr);
    }

    /**
     * Checks if it is zero length.
     * @return true if it is not empty, otherwise false.
     */
    public boolean isEmpty() {
        return size() == 0;
    }
}
