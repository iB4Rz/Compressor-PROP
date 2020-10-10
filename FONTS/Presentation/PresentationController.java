package Presentation;

import Domain.DomainController;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class PresentationController {
    /**
     * Navegation panel
     */
    private NavigationPanel navigator;
    /**
     * Indicates if it is compressed
     */
    private Boolean isCompressed = null;
    /**
     * Singleton instance
     */
    private static final PresentationController instance = new PresentationController();

    /**
     * Default constructor 
     * private to avoid external use of the constructor
     */
    private PresentationController() {
    }

    /**
     * Singleton getter
     * @return the instance
     */
    public static PresentationController getInstance() {
        return instance;
    }

    /**
     * Display the UI
     */
    public void DisplayUI() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame();
            }
        });
    }

    /**
     * Return if the realtive path is compressed
     * @param path realtive path to a file
     * @return if is ti compressed
     * @throws Exception if path is invalid or filetree not initialized
     */
    public boolean readFileTree(String path) throws Exception {
        isCompressed = DomainController.getInstance().readFileTree(path);
        navigator.refresh("");
        return isCompressed;
    }

    /**
     * Return filenames from the given relative path
     * @param path either "." or "foo/bar.." (replacing ".." with the rest of the path)
     * @return an array of filenames contained in the folder with path equal to path argument
     * @throws Exception if path is invalid or filetree not initialized
     */
    public String[] getFileNames(String path) throws Exception {
        return DomainController.getInstance().getFileNames(path);
    }

    /**
     * Return folder names from the given relative path
     * @param path either "." or "foo/bar.." (replacing ".." with the rest of the path)
     * @return an array of folder names contained in the folder with path equal to path argument
     * @throws Exception if path is invalid or filetree not initialized
     */
    public String[] getFolderNames(String path) throws Exception {
        return DomainController.getInstance().getFolderNames(path);
    }

    /**
     * Returns an array of all implemented compression algorithms for the given file in the path
     * @param path path of the file
     * @return a string array containing valid compression types for the file in the path
     * @throws Exception if the path does not point a file
     */
    public String[] getValidCompressionTypes(String path) throws Exception {
        return DomainController.getInstance().getValidCompressionTypes(path);
    }

    /**
     * Returns an array of all implemented parameters for the given algorithm
     * @param compressionType the compression type, either "LZW", "LZ78", "LZSS" or "JPEG"
     * @return a string array containing valid parameters types for the file in the path
     * @throws Exception the path does not point a file
     */
    public String[] getValidCompressionParameters(String compressionType) throws Exception {
        return DomainController.getInstance().getValidCompressionParameters(compressionType);
    }

    /**
     * Returns true if the file in the path is a ppm image
     * @param path relative path to a file
     * @return true if the file in the path is a ppm image, false otherwise
     * @throws Exception if the path does not point a file
     */
    public boolean isFileImage(String path) throws Exception {
        return DomainController.getInstance().isFileImage(path);
    }

    /**
     * Sets the compression type for the file in the given path
     * @param path relative path to a file
     * @param Type the compression type, either "LZW", "LZ78", "LZSS" or "JPEG"
     * @throws Exception if the file does not exist, or the compression type does not support the file 
     * or if changing the compression of an already compressed file, or the compression type does not exist
     */
    public void setCompressionType(String path, String Type) throws Exception {
        DomainController.getInstance().setCompressionType(path, Type);
    }

    /**
     * Returns the current compression type for the file in the given path
     * @param path relative path to a file
     * @return the compression type, either "LZW", "LZ78", "LZSS" or "JPEG"
     * @throws Exception if the file does not exist
     */
    public String getCompressionType(String path) throws Exception {
        return DomainController.getInstance().getCompressionType(path);
    }

    /**
     * Returns the current parameter of the algorithm for the file in the given path
     * @param path relative path to a file
     * @return the paramater selected
     * @throws Exception if the file does not exist
     */
    public String getCompressionParameter(String path) throws Exception {
        return DomainController.getInstance().getCompressionParameter(path);
    }

    /**
     * Set the current compression type for the file in the given path
     * @param path relative path to a file
     * @param arg the algorithm 
     * @throws Exception if the file does not exist
     */
    public void setCompressionParameter(String path, String arg) throws Exception {
        DomainController.getInstance().setCompressionParameter(path, arg);
    }

    /**
     * Compresses the entire file tree to the given file path
     * @param OutputFilePath Path to the resulting compressed file
     * @throws Exception if any of the compressions fails or invalid OutputFilePath
     */
    public void compressTo(String OutputFilePath) throws Exception {
        DomainController.getInstance().compressTo(OutputFilePath);
        navigator.refresh();
        JOptionPane.showMessageDialog(new JFrame(), "Compression Finished!");
    }

    /**
     * Decompresses the entire file tree into the given folder path
     * @param OutputFolderPath Path to the folder to save the decompressed data
     * @throws Exception if any of the decompressions fails, or invalid OutputFolderPath
     */
    public void decompressTo(String OutputFolderPath) throws Exception {
        DomainController.getInstance().decompressTo(OutputFolderPath);
        navigator.refresh();
        JOptionPane.showMessageDialog(new JFrame(), "Decompression Finished!");
    }

    /**
     * Returns a decompressed document from the compressed or not compressed filetree
     * @param path relative path to the file
     * @return String that contains the entire document in UTF-8
     * @throws Exception if i/o error, or the decompression fails
     */
    public String getDocument(String path) throws Exception {
        return DomainController.getInstance().getDocument(path);
    }

    /**
     * Returns a byte array representing the image from the path
     * @param path relative path to a ppm image
     * @return byte[] where the first 4 bytes represent width, 4 next the height and then 3 bytes per color (on byte per RGB component)
     * @throws Exception if the image does not exist or is malformed
     */
    public byte[] getImage(String path) throws Exception {
        return DomainController.getInstance().getImage(path);
    }

    /**
     * Returns a byte array representing the image from the path after being compressed with the current algorithm and argument
     * @param path relative path to a ppm image
     * @return byte[] where the first 4 bytes represent width, 4 next the height and then 3 bytes per color (on byte per RGB component)
     * @throws Exception if the image does not exist or is malformed
     */
    public byte[] getImageAfterLossyCompression(String path) throws Exception {
        return DomainController.getInstance().getImageAfterLossyCompression(path);
    }

    /**
     * Indicates the mode
     * @return if is compressed
     */
    public Boolean isCompressed() {
        return isCompressed;
    }

    /**
     * Sets the navigator
     * @param np the navigation panel
     */
    public void setNavigator(NavigationPanel np) {
        navigator = np;
    }

    /**
     * Returns the total time of the last compress/decompress operation
     * @return time in miliseconds
     */
    public long getTotalTimeStat() {
        return DomainController.getInstance().getTotalTimeStat();
    }

    /**
     * Returns the total size of the input data from the last compress/decompress operation
     * @return size in bytes
     */
    public long getTotalInputSizeStat() {
        return DomainController.getInstance().getTotalInputSizeStat();
    }

    /**
     * Returns the total size of the output data from the last compress/decompress operation
     * @return size in bytes
     */
    public long getTotalOutputSizeStat() {
        return DomainController.getInstance().getTotalOutputSizeStat();
    }

    /**
     * Returns the time of the last compress/decompress operation for the file in the given path
     * @param path relative path to a file
     * @return time in miliseconds
     * @throws Exception if the file does not exist
     */
    public long getFileTimeStat(String path) throws Exception {
        return DomainController.getInstance().getFileTimeStat(path);
    }

    /**
     * Returns the input size of the last compress/decompress operation for the file in the given path
     * @param path relative path to a file
     * @return size in bytes
     * @throws Exception if the file does not exist
     */
    public long getFileInputSizeStat(String path) throws Exception {
        return DomainController.getInstance().getFileInputSizeStat(path);
    }

    /**
     * Returns the output size of the last compress/decompress operation for the file in the given path
     * @param path relative path to a file
     * @return size in bytes
     * @throws Exception if the file does not exist
     */
    public long getFileOutputSizeStat(String path) throws Exception {
        return DomainController.getInstance().getFileOutputSizeStat(path);
    }
}