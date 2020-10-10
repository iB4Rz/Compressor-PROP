package Domain;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.PriorityQueue;

/**
 * @author Alexandre Perez
 */

/**
 * Huffman encoder, compress an InputStream to an OutputStream and decompress a valid InputStream to an OutputStream
 */
public class Huffman extends Algorithm {

    /**
     * alphabet size of ASCII
     */ 
    private static final int R = 256;
    private int nBits = 32;

    /**
     * sets the max size in bytes of the inputstream so the header can be optimized
     * @param maxSizeInBytes maximum size in bytes of the input stream (use 2^31-1 if unknown or default), this value its needed for decompress
     */
    public void setMaxSizeHint(int maxSizeInBytes) {
        nBits = maxSizeInBytes == 0 ? 1 : 33 - Integer.numberOfLeadingZeros(maxSizeInBytes - 1);
        if ((nBits < 0) || (nBits > 32))
            throw new IllegalArgumentException("maxSizeInBytes must be in [0, 2^31-1]");
    }

    /**
     * Compresses the input stream into the output stream given the max size of the input stream.
     * @param is InputStream with an amount of data between 0 and maxSizeInBytes
     * @param os OutputStream will be written with the compressed InputStream
     * @throws IOException If reading/writting to the input and output streams fails
     */
    @Override
    public void compress(InputStream is, OutputStream os) throws IOException {
        // read the input
        BitOutputStream bos = new BitOutputStream(os);
        ByteArrayOutputStream aux = new ByteArrayOutputStream();
        int[] freq = new int[R];
        int next;
        while ((next = is.read()) >= 0) {
            freq[next]++;
            aux.write(next);
        }
        aux.flush();
        byte[] input = aux.toByteArray();
        aux.close();

        // write size in bytes of the original input stream
        for (int i = 0; i < nBits; ++i) {
            int val = (input.length >>> i) & 0x01;
            bos.write1Bit(val);
        }

        if (input.length < 1) {
            bos.flush();
            return; // just store (0) for an empty stream
        }
        // Huffman trie
        Node root = generateTrie(freq);

        // code table
        ByteArray[] st = new ByteArray[R];
        generateCodeTable(st, root, new ByteArray());

        // serialize trie for decoder to understand the stream
        serializeTrie(bos, root);

        // encode all the stream
        for (int i = 0; i < input.length; i++) {
            ByteArray code = st[input[i] & 0xFF];
            byte[] ba = code.getBytes();
            for (int j = 0; j < code.size(); j++) {
                if (ba[j] == '0') {
                    bos.write1Bit(false);
                } else if (ba[j] == '1') {
                    bos.write1Bit(true);
                } else
                    throw new IllegalStateException("Illegal state");
            }
        }
        bos.flush();
    }

    /**
     * Decompresses the input stream into the output stream given the max size of the input stream.
     * @param is InputStream with an amount of data between 0 and maxSizeInBytes
     * @param os OutputStream will be written with the compressed InputStream
     * @throws IOException If reading/writting to the input and output streams fails
     */
    @Override
    public void decompress(InputStream is, OutputStream os) throws IOException {
        BitInputStream bis = new BitInputStream(is);

        int length = 0;
        for (int i = 0; i < nBits; ++i) {
            int next;
            if ((next = bis.read1Bit()) < 0)
                throw new IOException();
            length |= (next << i);
        }
        if (length < 1)
            return; // return if decompressed is an empty stream

        // generate Huffman trie from input stream
        Node root = deserializeTrie(bis);

        // decode all the stream
        for (int i = 0; i < length; i++) {
            Node x = root;
            while (!x.isLeaf()) {
                int next;
                if ((next = bis.read1Bit()) < 0)
                    throw new IOException();
                boolean bit = (next != 0);
                if (bit)
                    x = x.right;
                else
                    x = x.left;
            }
            int out = ((int) (x.code) & 0xFF);
            os.write(out);
        }
        os.flush();
    }

    /**
     * Creates the trie used by the huffman encoding at compression time
     * @param freq array of R size that contains the frequency of the codes with integer value of the index
     * @return the root of the generated trie
     */
    private Node generateTrie(int[] freq) {
        // init PQ with trees equal to leaves
        PriorityQueue<Node> pq = new PriorityQueue<Node>();
        for (short i = 0; i < R; i++)
            if (freq[i] > 0)
                pq.add(new Node(i, freq[i], null, null));
        if (pq.size() == 1) {
            if (freq['\0'] == 0)
                pq.add(new Node((short) '\0', 0, null, null));
            else
                pq.add(new Node((short) '\1', 0, null, null));
        }

        // merge smallest trees
        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            Node parent = new Node((short) '\0', left.freq + right.freq, left, right);
            pq.add(parent);
        }
        return pq.poll();
    }

    /**
     * Generates a lookup table st from the codes and their encodings
     * @param st Lookup table to fill (this function is recursive so it can be partially populated)
     * @param x Current node that isn't traversed for the current code (Node.code) yet
     * @param s ByteArray with the codified path or partial path for the current code (Node.code)
     */
    private void generateCodeTable(ByteArray[] st, Node x, ByteArray s) {
        if (!x.isLeaf()) {
            generateCodeTable(st, x.left, s.concatenate((byte) '0'));
            generateCodeTable(st, x.right, s.concatenate((byte) '1'));
        } else {
            st[x.code & 0xFF] = s;
        }
    }

    /**
     * Writes the trie used by the huffman encoding to the output stream codified as bits
     * @param bos BitOutputStream where trie with root x will be encoded and written
     * @param x Root node of the huffman encoding trie that will be written to the BitOutputStream
     * @throws IOException if writing to the BitOutputStream fails
     */
    private void serializeTrie(BitOutputStream bos, Node x) throws IOException {
        if (x.isLeaf()) {
            bos.write1Bit(true);
            byte out = (byte) (x.code & 0xFF);
            bos.write8Bit(out);
            return;
        }
        bos.write1Bit(false);
        serializeTrie(bos, x.left);
        serializeTrie(bos, x.right);
    }

    /**
     * Generates the huffman encoding trie from the BitInputStream
     * @param bis BitInputStream from where the codified tree will be read
     * @return the root node for the generated huffman encoding tree
     * @throws IOException if writting to the BitInputStream fails
     */
    private Node deserializeTrie(BitInputStream bis) throws IOException {
        int next;
        if ((next = bis.read1Bit()) < 0)
            throw new IOException();
        boolean isLeaf = (next != 0);
        if (isLeaf) {
            short tmp = (short) (bis.read8Bit());
            return new Node(tmp, -1, null, null);
        } else {
            return new Node((short) '\0', -1, deserializeTrie(bis), deserializeTrie(bis));
        }
    }



    // Huffman trie node with comparable (needed to use it with PriorityQueue)
    private class Node implements Comparable<Node> {
        /**
        * natural value of an ascii character
        */ 
        private final short code;
        /**
        * frequency of the code
        */ 
        private final int freq;
        /**
        * left child from this node
        */ 
        private final Node left;
        /**
        * right child from this node
        */ 
        private final Node right;

        /**
         * Constructor of the Node class
         * @param code code (ascii) value as a natural
         * @param freq frequency of the code
         * @param left left node this instance will be attached
         * @param right right node this instance will be attached
         */
        Node(short code, int freq, Node left, Node right) {
            this.code = code;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        /**
         * Check if this node is a leaf.
         * @return true if this node is a leaf, false otherwise
         */
        private boolean isLeaf() {
            assert ((left == null) && (right == null)) || ((left != null) && (right != null));
            return (left == null) && (right == null);
        }

        /**
         * Compare this instance with the argument
         * @param that Node to compare with
         * @return substraction between freq and the argument's freq value
         */
        public int compareTo(Node that) { // needed to use with the priority queue
            return this.freq - that.freq;
        }
    }
}

