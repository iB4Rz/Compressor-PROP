package Domain;

import Persistence.PersistenceController;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DomainController {
    /**
     * Singleton instance
     */
    private static final DomainController instance = new DomainController();

    /**
     * private constructor
     */
    private DomainController() {}

    /**
     * singleton instance getter
     * @return singleton instance
     */
    public static DomainController getInstance() {
        return instance;
    }
    
    /**
     * loads a filetree and returns if was compressed
     * @param path path to a filetree
     * @return tru if it was a compressed file, false otherwise
     * @throws IOException if io exception
     */
    public boolean readFileTree(String path) throws IOException {
        PersistenceController.getInstance().readFileTree(path);
        return PersistenceController.getInstance().isFileTreeCompressed();
    }

    /**
     * Return filenames from the given relative path
     * @param pathToParentFolder either "."/"" or "foo/bar.." (replacing ".." with the rest of the path)
     * @return an array of filenames contained in the folder with path equal to path argument
     * @throws Exception if path is invalid or filetree not initialized
     */
    public String[] getFileNames(String pathToParentFolder) throws Exception {
        if (pathToParentFolder.length() == 0)
            pathToParentFolder = ".";
        return PersistenceController.getInstance().getFileNames(pathToParentFolder);
    }

    /**
     * Return folder names from the given relative path
     * @param pathToParentFolder either "." or "foo/bar.." (replacing ".." with the rest of the path)
     * @return an array of folder names contained in the folder with path equal to path argument
     * @throws Exception if path is invalid or filetree not initialized
     */
    public String[] getFolderNames(String pathToParentFolder) throws Exception {
        if (pathToParentFolder.length() == 0)
            pathToParentFolder = ".";
        return PersistenceController.getInstance().getFolderNames(pathToParentFolder);
    }

    /**
     * Returns an array of all implemented compression algorithms for the given file in the path
     * @param path path of the file
     * @return a string array containing valid compression types for the file in the path
     * @throws Exception if the path does not point a file
     */
    public String[] getValidCompressionTypes(String path) throws Exception {
        if (isFileImage(path)) {
            return new String[] {"JPEG"};
        }
        else {
            return new String[] {"LZW", "LZ78", "LZSS"};
        }
    }

    /**
     * Returns the valid compression arguments or presets for a given compression type
     * @param compressionType String, either "LZW", "LZ78", "LZSS" or "JPEG"
     * @return a String[] with all valid options for the given compressionType
     * @throws Exception if compressionType is not implemented
     */
    public String[] getValidCompressionParameters(String compressionType) throws Exception {
        switch (compressionType) {
            case "LZW":
                return new String[] {"8", "9", "10", "11", "12", "13", "14", "15", 
                    "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26",
                    "27", "28", "29", "30", "31"};
            case "LZ78":
                return new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", 
                    "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", 
                    "24", "25", "26", "27", "28", "29", "30", "31"};
            case "LZSS":
                return new String[0];
            case "JPEG":
                return new String[] {"Q0", "Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "Q7", "Q8", "Q9", 
                    "Q10", "Q11", "Q12", "MAX", "MIN", "DEFAULT", "JPEGStandard"};
            default:
                throw new Exception("Invalid Compression Type!");
        }
    }

    /**
     * Gets the default compression parameter for the given compression type
     * @param compressionType either "LZW", "LZ78", "LZSS" or "JPEG"
     * @return the default parameter for the type
     * @throws Exception if compressionType is not "LZW", "LZ78", "LZSS" or "JPEG"
     */
    public String getDefaultCompressionParameter(String compressionType) throws Exception {
        switch (compressionType) {
            case "LZW":
                return "12";
            case "LZ78":
                return "12";
            case "LZSS":
                return null;
            case "JPEG":
                return "DEFAULT";
            default:
                throw new Exception("Invalid Compression Type!");
        }
    }

    /**
     * Gets the default compression type
     * @param isPPMImage true if file is a ppm image
     * @return the default compression type
     */
    public String getDefaultCompressionType(boolean isPPMImage) {
        if (isPPMImage) {
            return "JPEG";
        }
        return "LZW";
    }

    /**
     * Returns true if the file in the path is a ppm image
     * @param path relative path to a file
     * @return true if the file in the path is a ppm image, false otherwise
     * @throws Exception if the path does not point a file
     */
    public boolean isFileImage(String path) throws Exception {
        return PersistenceController.getInstance().isFileImage(path);
    }

    /**
     * Sets the compression type for the file in the given path
     * @param path relative path to a file
     * @param Type the compression type, either "LZW", "LZ78", "LZSS" or "JPEG"
     * @throws Exception if the file does not exist, or the compression type does not support the file 
     * or if changing the compression of an already compressed file, or the compression type does not exist
     */
    public void setCompressionType(String path, String Type) throws Exception {
        PersistenceController.getInstance().setCompressionType(path, Type);
    }

    /**
     * Returns the current compression type for the file in the given path
     * @param path relative path to a file
     * @return the compression type, either "LZW", "LZ78", "LZSS" or "JPEG"
     * @throws Exception if the file does not exist
     */
    public String getCompressionType(String path) throws Exception {
        return PersistenceController.getInstance().getCompressionType(path);
    }

    /**
     * Returns the current compression parameter for the given file
     * @param path relative path to a file
     * @return String representing a valid compression parameter
     * @throws Exception if the file does not exist
     */
    public String getCompressionParameter(String path) throws Exception {
        return PersistenceController.getInstance().getCompressionParameter(path);
    }

    /**
     * Sets the compression parameter for a file
     * @param path relative path to a file
     * @param arg valid compression parameter
     * @throws Exception if the file does not exist
     */
    public void setCompressionParameter(String path, String arg) throws Exception {
        PersistenceController.getInstance().setCompressionParameter(path, arg);
    }

    /**
     * Compresses the entire file tree to the given file path
     * @param OutputFilePath Path to the resulting compressed file
     * @throws Exception if any of the compressions fails or invalid OutputFilePath
     */
    public void compressTo(String OutputFilePath) throws Exception {
        // traverses the filetree calling chainCompress on each file
        PersistenceController.getInstance().compressFiletree(OutputFilePath);
    }

    /**
     * Decompresses the entire file tree into the given folder path
     * @param OutputFolderPath Path to the folder to save the decompressed data
     * @throws Exception if any of the decompressions fails, or invalid OutputFolderPath
     */
    public void decompressTo(String OutputFolderPath) throws Exception {
        // traverses the filetree calling chainDecompress on each file
        PersistenceController.getInstance().decompressFiletree(OutputFolderPath);
    }

    /**
     * Returns a decompressed document from the compressed or not compressed filetree
     * @param Path relative path to the file
     * @return String that contains the entire document in UTF-8
     * @throws Exception if i/o error, or the decompression fails
     */
    public String getDocument(String Path) throws Exception {
        return PersistenceController.getInstance().getDocument(Path);
    }

    /**
     * Returns a byte array representing the image from the path
     * @param Path relative path to a ppm image
     * @return byte[] where the first 4 bytes represent width, 4 next the height and then 3 bytes per color (on byte per RGB component)
     * @throws Exception if the image does not exist or is malformed
     */
    public byte[] getImage(String Path) throws Exception {
        PPMTranslator ppmt = new PPMTranslator(PersistenceController.getInstance().getImage(Path));
        return presentationEncodeImage(ppmt);
    }

    /**
     * Returns a byte array representing the image from the path after being compressed with the current algorithm and argument
     * @param Path relative path to a ppm image
     * @return byte[] where the first 4 bytes represent width, 4 next the height and then 3 bytes per color (on byte per RGB component)
     * @throws Exception if the image does not exist or is malformed
     */
    public byte[] getImageAfterLossyCompression(String Path) throws Exception {
        PPMTranslator ppmt = new PPMTranslator(PersistenceController.getInstance().getImageAfterLossyCompression(Path));
        return presentationEncodeImage(ppmt);
    }

    /**
     * Creates a byte array representing an image from a PPMTranslator
     * @param ppmt Initialized PPMTranslator for reading
     * @return byte[] where the first 4 bytes represent width, 4 next the height and then 3 bytes per color (on byte per RGB component)
     * @throws Exception if the ppmt input source is malformed
     */
    private byte[] presentationEncodeImage(PPMTranslator ppmt) throws Exception {
        int w = ppmt.getWidth();
        byte[] wa = toArray(w);
        int h = ppmt.getHeight();
        byte[] ha = toArray(h);
        byte[] result = new byte[8 + (w*h*3)];
        System.arraycopy(wa, 0, result, 0, 4);
        System.arraycopy(ha, 0, result, 4, 4);
        for (int i = 8; i < result.length; ++i)
            result[i] = (byte)ppmt.getNextComponent(); // unsigned encoding
        return result;
    }

    /**
     * Encodes an integer into 4 bytes
     * @param value any integer
     * @return byte[4] containing the bytes from value from hight to low bits
     */
    private byte[] toArray(int value) {
        byte[] result = new byte[4];
        result[0] = (byte)((value >> 24) & 0x000000FF);
        result[1] = (byte)((value >> 16) & 0x000000FF);
        result[2] = (byte)((value >> 8) & 0x000000FF);
        result[3] = (byte)(value & 0x000000FF);
        return result;
    }

    /**
     * Returns the total time of the last compress/decompress operation
     * @return time in miliseconds
     */
    public long getTotalTimeStat() {
        return PersistenceController.getInstance().getTotalTimeStat();
    }

    /**
     * Returns the total size of the input data from the last compress/decompress operation
     * @return size in bytes
     */
    public long getTotalInputSizeStat() {
        return PersistenceController.getInstance().getTotalInputSizeStat();
    }

    /**
     * Returns the total size of the output data from the last compress/decompress operation
     * @return size in bytes
     */
    public long getTotalOutputSizeStat() {
        return PersistenceController.getInstance().getTotalOutputSizeStat();
    }

    /**
     * Returns the time of the last compress/decompress operation for the file in the given path
     * @param path relative path to a file
     * @return time in miliseconds
     * @throws Exception if the file does not exist
     */
    public long getFileTimeStat(String path) throws Exception {
        return PersistenceController.getInstance().getFileTimeStat(path);
    }

    /**
     * Returns the input size of the last compress/decompress operation for the file in the given path
     * @param path relative path to a file
     * @return size in bytes
     * @throws Exception if the file does not exist
     */
    public long getFileInputSizeStat(String path) throws Exception {
        return PersistenceController.getInstance().getFileInputSizeStat(path);
    }

    /**
     * Returns the output size of the last compress/decompress operation for the file in the given path
     * @param path relative path to a file
     * @return size in bytes
     * @throws Exception if the file does not exist
     */
    public long getFileOutputSizeStat(String path) throws Exception {
        return PersistenceController.getInstance().getFileOutputSizeStat(path);
    }

    /**
     * Compresses an InputStream to an OutputStream with the compression algorithm given by compressionType
     * @param is InputStream containing valid uncompressed data
     * @param os OutputStream will contain the compressed data from InputStream
     * @param compressionType String that specifies the compression algorithm, either "LZW", "LZ78", "LZSS" or "JPEG"
     * @param arg0 String with an option for the compression type
     * @throws Exception if the compression fails or i/o error
     */
    public void chainCompress(InputStream is, OutputStream os, String compressionType, String arg0) throws Exception {
        Algorithm alg;
        switch (compressionType) {
            case "LZW":
                LZW lzw = new LZW();
                lzw.setDictionarySize(Integer.valueOf(arg0));
                alg = lzw;
                break;
            case "LZ78":
                LZ78 lz78 = new LZ78();
                lz78.setDictionarySize(Integer.valueOf(arg0));
                alg = lz78;
                break;
            case "LZSS":
                alg = new LZSS();
                break;
            case "JPEG":
                JPEG jpeg = new JPEG(new Huffman());
                jpeg.setQuantizationTables(PersistenceController.getInstance().getLuminanceTable(arg0), PersistenceController.getInstance().getChrominanceTable(arg0));
                alg = jpeg;
                break;
            default:
                throw new Exception("Invalid Compression Type!");
        }
        alg.compress(is, os);
    }

    /**
     * Decompresses an InputStream to an OutputStream with the compression algorithm given by compressionType
     * @param is InputStream containing valid compressed data
     * @param os OutputStream will contain the decompressed data from InputStream
     * @param compressionType String that specifies the compression algorithm, either "LZW", "LZ78", "LZSS" or "JPEG"
     * @throws Exception if the decompression fails or i/o error
     */
    public void chainDecompress(InputStream is, OutputStream os, String compressionType) throws Exception {
        Algorithm alg;
        switch (compressionType) {
            case "LZW":
                alg = new LZW();
                break;
            case "LZ78":
                alg = new LZ78();
                break;
            case "LZSS":
                alg = new LZSS();
                break;
            case "JPEG":
                alg = new JPEG(new Huffman());
                break;
            default:
                throw new Exception("Invalid Compression Type!");
        }
        alg.decompress(is, os);
    }
    
}