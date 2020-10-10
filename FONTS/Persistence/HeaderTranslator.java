package Persistence;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class HeaderTranslator {
    /**
     * boolean that tells if the read filetree is compressed or not
     */
    private Boolean compressedFileTree = null;
    /**
     * size of the header
     */
    private long headerSize = 0;

    /**
     * returns if the filetree is compressed
     * @return true if its compressed, false otherwise
     */
    public Boolean fileTreeIsCompressed() {
        return compressedFileTree;
    }

    /**
     * header size getter
     * @return header size
     */
    public long getReadHeaderSize() {
        return headerSize;
    }

    /**
     * reads the filetree in the path
     * @param path canonical system path
     * @return root folder of the read path
     * @throws IOException if io error
     */
    public Folder readFileTree(String path) throws IOException {
        Folder FileTree;
        try {
            FileTree = new Folder("root", null);
            InputStreamWatcher isw = new InputStreamWatcher(new BufferedInputStream(new FileInputStream(path)));
            readCompressedFileTree(isw, FileTree);
            headerSize = isw.getReadBytes();
            compressedFileTree = true;
        } catch (Exception e) {
            FileTree = new Folder("root", null);
            readUncompressedFileTree(new File(path), FileTree);
            compressedFileTree = false;
        }
        
        return FileTree;
    }

    /**
     * reads a compressed file tree
     * @param is input stream containing the compressed file tree
     * @param parentFolder parent folder of the given file tree
     * @throws Exception if i/o exception
     */
    private void readCompressedFileTree(InputStream is, Folder parentFolder) throws Exception {
        int next = is.read();
        while ((next > 0) && (next <= 4)) {
            byte[] buf0 = new byte[8];
            is.read(buf0);
            long index = toLong(buf0);
            int size = is.read();
            byte[] buf1 = new byte[size];
            is.read(buf1);
            String name = new String(buf1, "UTF-8");
            Archive file = new Archive(parentFolder.getPath() + System.getProperty("file.separator") + name);
            file.setCompressionType(decodeCompressionType((byte)next));
            file.setHeaderIndex(index);
            parentFolder.addFile(file);
            next = is.read();
        }
        while (next == 0) {
            int size = is.read();
            byte[] buff = new byte[size];
            is.read(buff);
            String name = new String(buff, "UTF-8");
            Folder folder = new Folder(name, parentFolder);
            readCompressedFileTree(is, folder);
            next = is.read();
        }
        if (next == 0x05) {
            return;
        }
        else throw new Exception("Invalid file type");
    }

    /**
     * reads a not compressed file tree
     * @param node file or folder inside parentFolder
     * @param parentFolder folder object, parent of node
     * @throws IOException if io exception
     */
    private void readUncompressedFileTree(File node, Folder parentFolder) throws IOException {
        if (node.isFile())
            parentFolder.addFile(new Archive(node.getCanonicalPath()));
        else {
            Folder folder = new Folder(node.getName(), parentFolder);
            File[] files = node.listFiles();
            for (File file : files)
                readUncompressedFileTree(file, folder);
        }
    }
    
    /**
     * writes the compressed file header with some uninitialized parameters
     * @param os output stream where the header will be written
     * @param parentFolder root of the hierarchy that will be written into the header
     * @throws Exception if io error
     */
    public void reserveHeader(OutputStream os, Folder parentFolder) throws Exception {
        reserve(os, parentFolder);
        os.flush();
    }
    
    /**
     * reserves the space needed to codify a folder in the header
     * @param os output stream where the header will be written
     * @param parentFolder folder that will be traversed
     * @throws Exception if io error
     */
    private void reserve(OutputStream os, Folder parentFolder) throws Exception {
        Archive[] files = parentFolder.getFiles();
        for (Archive file : files) {
            reserveFileHeader(os, file);
        }
        Folder[] folders = parentFolder.getFolders();
        for (Folder folder : folders) {
            os.write(0x00); // Type: Folder
            byte[] name = folder.getName().getBytes("UTF-8");
            os.write(name.length);
            os.write(name);
            reserve(os, folder);
        }
        os.write(0x05);
    }

    /**
     * reserves the space in the header for a file
     * @param os output stream where the header will be written
     * @param file file that will be encoded in the header
     * @throws Exception if io error
     */
    private void reserveFileHeader(OutputStream os, Archive file) throws Exception {
        byte type = encodeCompressionType(file.getCompressionType());
        os.write(type);
        for (int i = 0; i < 8; ++i) {
            os.write(0);
        }
        byte[] name = file.getFilename().getBytes("UTF-8");
        os.write(name.length);
        os.write(name);
    }

    /**
     * decodes the compression type from a byte
     * @param b valid encode
     * @return compression type string
     * @throws Exception if b does not encode any compression algorithm
     */
    String decodeCompressionType(byte b) throws Exception {
        switch (b) {
            case 0x01:
                return "LZW";
            case 0x02:
                return "LZ78";
            case 0x03:
                return "LZSS";
            case 0x04:
                return "JPEG";
            default:
                throw new Exception("Invalid compression type");
        }
    }

    /**
     * encodes the compression type to a byte
     * @param compression compression type
     * @return encoded byte
     * @throws Exception if compression its not a valid compression type
     */
    byte encodeCompressionType(String compression) throws Exception {
        switch (compression) {
            case "LZW":
                return 0x01;
            case "LZ78":
                return 0x02;
            case "LZSS":
                return 0x03;
            case "JPEG":
                return 0x04;
            default:
                throw new Exception("Invalid compression type");
        }
    }
    
    /**
     * initializes header values in a valid uninitialized header
     * @param path path to the compressed file
     * @param parentFolder root folder encoded in the header
     * @throws Exception if io error
     */
    public void setHeaderValues(String path, Folder parentFolder) throws Exception {
        RandomAccessFile raf = new RandomAccessFile(path, "rw");
        setHeader(raf, parentFolder);
        raf.close();
    }

    /**
     * initializes header values in a valid uninitialized header
     * @param header compressed file
     * @param parentFolder root folder encoded in the header
     * @throws Exception if io error
     */
    private void setHeader(RandomAccessFile header, Folder parentFolder) throws Exception {
        Archive[] files = parentFolder.getFiles();
        for (Archive file : files) {
            header.skipBytes(1);
            header.write(toArray(file.getHeaderIndex()));
            header.skipBytes(header.read()); // skip filename length and filename
        }
        Folder[] folders = parentFolder.getFolders();
        for (Folder folder : folders) {
            header.skipBytes(1);
            header.skipBytes(header.read()); // skip folder name length and folder name
            setHeader(header, folder);
        }
        header.skipBytes(1);
    }
    
    /**
     * encodes 8 bytes into a long variable
     * @param bytes array of length 8
     * @return long value
     */
    private long toLong(byte[] bytes) {
        long result = 0;
        result |= (long)(bytes[0]) << 56;
        result |= ((long)(bytes[1]) << 48) & 0x00FF000000000000L;
        result |= ((long)(bytes[2]) << 40) & 0x0000FF0000000000L;
        result |= ((long)(bytes[3]) << 32) & 0x000000FF00000000L;
        result |= ((long)(bytes[4]) << 24) & 0x00000000FF000000L;
        result |= ((long)(bytes[5]) << 16) & 0x0000000000FF0000L;
        result |= ((long)(bytes[6]) << 8) & 0x000000000000FF00L;
        result |= (long)(bytes[7]) & 0x00000000000000FFL;
        return result;
    }

    /**
     * encodes a long into 8 bytes
     * @param value any long value
     * @return array of 8 bytes
     */
    private byte[] toArray(long value) {
        byte[] result = new byte[8];
        result[0] = (byte)((value >> 56) & 0x00000000000000FFL);
        result[1] = (byte)((value >> 48) & 0x00000000000000FFL);
        result[2] = (byte)((value >> 40) & 0x00000000000000FFL);
        result[3] = (byte)((value >> 32) & 0x00000000000000FFL);
        result[4] = (byte)((value >> 24) & 0x00000000000000FFL);
        result[5] = (byte)((value >> 16) & 0x00000000000000FFL);
        result[6] = (byte)((value >> 8) & 0x00000000000000FFL);
        result[7] = (byte)(value & 0x00000000000000FFL);
        return result;
    }

}