package Persistence;

import Domain.DomainController;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PersistenceController {
    /**
     * Singleton instance
     */
    private static final PersistenceController instance = new PersistenceController();
    /**
     * FileTree root instance
     */
    private Folder FileTree;
    /**
     * Total statistics of the last compression or decompression
     */
    private Statistics totalStats = new Statistics();
    /**
     * Header translator instance
     */
    private HeaderTranslator headerTranslator;
    /**
     * path of the opened filetree
     */
    private String openedPath;

    /**
     * private constructor
     */
    private PersistenceController() {}

    /**
     * singleton getter
     * @return this singleton instance
     */
    public static PersistenceController getInstance() {
        return instance;
    }

    /**
     * filetree reader
     * @param path path to a filetree
     * @throws IOException if error reading
     */
    public void readFileTree(String path) throws IOException {
        headerTranslator = new HeaderTranslator();
        totalStats = new Statistics();
        FileTree = headerTranslator.readFileTree(path);
        openedPath = path;
    }

    /**
     * returns if the read filetree is compressed
     * @return true if the filetree is compressed, false otherwise
     */
    public Boolean isFileTreeCompressed() {
        return headerTranslator.fileTreeIsCompressed();
    }

    /**
     * Return filenames from the given relative path
     * @param pathToParentFolder either "."/"" or "foo/bar.." (replacing ".." with the rest of the path)
     * @return an array of filenames contained in the folder with path equal to path argument
     * @throws Exception if path is invalid or filetree not initialized
     */
    public String[] getFileNames(String pathToParentFolder) throws Exception {
        return Folder.getFolder(FileTree.getRoot(), pathToParentFolder).getFileNames();
    }

    /**
     * Return folder names from the given relative path
     * @param pathToParentFolder either "." or "foo/bar.." (replacing ".." with the rest of the path)
     * @return an array of folder names contained in the folder with path equal to path argument
     * @throws Exception if path is invalid or filetree not initialized
     */
    public String[] getFolderNames(String pathToParentFolder) throws Exception {
        return Folder.getFolder(FileTree.getRoot(), pathToParentFolder).getFolderNames();
    }

    /**
     * Sets the compression type for the file in the given path
     * @param path relative path to a file
     * @param Type the compression type, either "LZW", "LZ78", "LZSS" or "JPEG"
     * @throws Exception if the file does not exist, or the compression type does not support the file 
     * or if changing the compression of an already compressed file, or the compression type does not exist
     */
    public void setCompressionType(String path, String Type) throws Exception {
        if (isFileTreeCompressed()) {
            throw new Exception("Cannot change compression algorithm of a compressed file!");
        }
        Archive f = Folder.getFile(FileTree.getRoot(), path);
        f.setCompressionType(Type);
    }

    /**
     * Returns the current compression type for the file in the given path
     * @param path relative path to a file
     * @return the compression type, either "LZW", "LZ78", "LZSS" or "JPEG"
     * @throws Exception if the file does not exist
     */
    public String getCompressionType(String path) throws Exception {
        Archive f = Folder.getFile(FileTree.getRoot(), path);
        return f.getCompressionType().toString();
    }

    /**
     * Returns the total time of the last compress/decompress operation
     * @return time in miliseconds
     */
    public long getTotalTimeStat() {
        return totalStats.getExecutionTime();
    }

    /**
     * Returns the total size of the input data from the last compress/decompress operation
     * @return size in bytes
     */
    public long getTotalInputSizeStat() {
        return totalStats.getInputSize();
    }

    /**
     * Returns the total size of the output data from the last compress/decompress operation
     * @return size in bytes
     */
    public long getTotalOutputSizeStat() {
        return totalStats.getOutputSize();
    }

    /**
     * Returns the time of the last compress/decompress operation for the file in the given path
     * @param path relative path to a file
     * @return time in miliseconds
     * @throws Exception if the file does not exist
     */
    public long getFileTimeStat(String path) throws Exception {
        Archive f = Folder.getFile(FileTree.getRoot(), path);
        Statistics stats = f.getStatistics();
        return stats.getExecutionTime();
    }

    /**
     * Returns the input size of the last compress/decompress operation for the file in the given path
     * @param path relative path to a file
     * @return size in bytes
     * @throws Exception if the file does not exist
     */
    public long getFileInputSizeStat(String path) throws Exception {
        Archive f = Folder.getFile(FileTree.getRoot(), path);
        Statistics stats = f.getStatistics();
        return stats.getInputSize();
    }

    /**
     * Returns the output size of the last compress/decompress operation for the file in the given path
     * @param path relative path to a file
     * @return size in bytes
     * @throws Exception if the file does not exist
     */
    public long getFileOutputSizeStat(String path) throws Exception {
        Archive f = Folder.getFile(FileTree.getRoot(), path);
        Statistics stats = f.getStatistics();
        return stats.getOutputSize();
    }

    /**
     * Returns true if the file in the path is a ppm image
     * @param path relative path to a file
     * @return true if the file in the path is a ppm image, false otherwise
     * @throws Exception if the path does not point a file
     */
    public boolean isFileImage(String path) throws Exception {
        Archive f = Folder.getFile(FileTree.getRoot(), path);
        return f.isImage();
    }

    /**
     * luminance table getter
     * @param quality valid quality setting
     * @return 8x8 matrix of integers, luminance table
     */
    public final int[][] getLuminanceTable(String quality) {
        return JPEG_Quality.valueOf(quality).getLuminanceTable();
    }

    /**
     * chrominance table getter
     * @param quality valid quality setting
     * @return 8x8 matrix of integers, chrominance table
     */
    public final int[][] getChrominanceTable(String quality) {
        return JPEG_Quality.valueOf(quality).getChrominanceTable();
    }

    /**
     * Returns the current compression parameter for the given file
     * @param path relative path to a file
     * @return String representing a valid compression parameter
     * @throws Exception if the file does not exist
     */
    public String getCompressionParameter(String path) throws Exception {
        Archive f = Folder.getFile(FileTree.getRoot(), path);
        return f.getCompressionArgument();
    }

    /**
     * Sets the compression parameter for a file
     * @param path relative path to a file
     * @param arg valid compression parameter
     * @throws Exception if the file does not exist or trying to change parameter of a compressed file
     */
    public void setCompressionParameter(String path, String arg) throws Exception {
        if (isFileTreeCompressed()) {
            throw new Exception("Cannot change compression parameter of a compressed file!");
        }
        Archive f = Folder.getFile(FileTree.getRoot(), path);
        f.setCompressionArgument(arg);
    }

    /**
     * Gets the default compression parameter for the given compression type
     * @param type either "LZW", "LZ78", "LZSS" or "JPEG"
     * @return the default parameter for the type
     * @throws Exception if compressionType is not "LZW", "LZ78", "LZSS" or "JPEG"
     */
    public String getDefaultCompressionParameter(String type) throws Exception {
        return DomainController.getInstance().getDefaultCompressionParameter(type);
    }

    /**
     * checks if the given parameter is valid for the compression type
     * @param arg compression parameter
     * @param type compression type
     * @return true if its valid, false otherwise
     * @throws Exception if compression type is not valid
     */
    public boolean isCompressionParameterValid(String arg, String type) throws Exception {
        String[] args = DomainController.getInstance().getValidCompressionParameters(type);
        for (String valid : args) {
            if (arg.equals(valid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the default compression type
     * @param isPPMImage true if file is a ppm image
     * @return the default compression type
     */
    public String getDefaultCompressionType(boolean isPPMImage) {
        return DomainController.getInstance().getDefaultCompressionType(isPPMImage);
    }

    /**
     * Returns a decompressed document from the compressed or not compressed filetree
     * @param Path relative path to the file
     * @return String that contains the entire document in UTF-8
     * @throws Exception if i/o error, or the decompression fails
     */
    public String getDocument(String Path) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Archive f = Folder.getFile(FileTree.getRoot(), Path);
        if (isFileTreeCompressed()) {
            InputStream is = new BufferedInputStream(new FileInputStream(openedPath));
            is.skip(f.getHeaderIndex());
            DomainController.getInstance().chainDecompress(is, baos, f.getCompressionType().toString());
        }
        else {
            InputStream is = f.getInputStream();
            int next;
            while ((next = is.read()) >= 0) {
                baos.write(next);
            }
        }
        baos.flush();
        return new String(baos.toByteArray(), "UTF-8"); // UTF-8 Encoding
    }

    /**
     * Returns an input stream representing the image from the path
     * @param Path relative path to a ppm image
     * @return input stream containing a valid ppm image
     * @throws Exception if the image does not exist or is malformed
     */
    public InputStream getImage(String Path) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Archive f = Folder.getFile(FileTree.getRoot(), Path);
        if (!f.isImage()) throw new Exception(Path+" Not an image!");
        if (isFileTreeCompressed()) {
            InputStream is = new BufferedInputStream(new FileInputStream(openedPath));
            is.skip(f.getHeaderIndex());
            DomainController.getInstance().chainDecompress(is, baos, f.getCompressionType().toString());
            baos.flush();
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            return bais;
        }
        return f.getInputStream();
    }

    /**
     * Returns an InputStream representing the image from the path after being compressed with the current algorithm and argument
     * @param Path relative path to a ppm image
     * @return input stream containing a valid ppm image
     * @throws Exception if the image does not exist or is malformed
     */
    public InputStream getImageAfterLossyCompression(String Path) throws Exception {    
        if (isFileTreeCompressed()) {
            return getImage(Path);
        }
        Archive f = Folder.getFile(FileTree.getRoot(), Path);
        if (!f.isImage()) throw new Exception(Path+" Not an image!");
        ByteArrayOutputStream baos0 = new ByteArrayOutputStream();
        DomainController.getInstance().chainCompress(f.getInputStream(), baos0, f.getCompressionType().toString(), f.getCompressionArgument());
        ByteArrayInputStream bais1 = new ByteArrayInputStream(baos0.toByteArray());
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        DomainController.getInstance().chainDecompress(bais1, baos1, f.getCompressionType().toString());
        ByteArrayInputStream bais2 = new ByteArrayInputStream(baos1.toByteArray());
        return bais2;
    }

    /**
     * write the compressed header and compress every file of the filetree
     * @param outputPath output path of the compressed file
     * @throws Exception if io error
     */
    public void compressFiletree(String outputPath) throws Exception {
        Archive out = new Archive(outputPath);
        OutputStream os = new BufferedOutputStream(out.getOutputStream());
        OutputStreamWatcher osw = new OutputStreamWatcher(os);
        headerTranslator.reserveHeader(osw, FileTree.getRoot());
        totalStats.setInputSize(0);
        totalStats.setOutputSize(osw.getWrittenBytes()); // header size
        totalStats.setExecutionTime(0);
        traverseCompress(os, FileTree.getRoot());
        os.flush();
        os.close();
        headerTranslator.setHeaderValues(outputPath, FileTree.getRoot());
    }

    /**
     * Decompress entire filetree to a directory
     * @param outputPath directory path of destination
     * @throws Exception if io error
     */
    public void decompressFiletree(String outputPath) throws Exception {
        InputStream is = new BufferedInputStream(new FileInputStream(openedPath));
        is.skip(headerTranslator.getReadHeaderSize());
        traverseDecompress(is, FileTree.getRoot(), outputPath);
        is.close();
    }

    /**
     * traverse the filetree compressing every file with its configuration
     * @param os output stream pointing the compressed file
     * @param parentFolder current parent folder of the traverse
     * @throws Exception if io error
     */
    private void traverseCompress(OutputStream os, Folder parentFolder) throws Exception {
        Archive[] files = parentFolder.getFiles();
        for (Archive file : files) {
            Statistics stats = new Statistics();
            InputStreamWatcher isw = new InputStreamWatcher(file.getInputStream());
            OutputStreamWatcher osw = new OutputStreamWatcher(os);
            long timeStart = System.currentTimeMillis();
            DomainController.getInstance().chainCompress(isw, osw, file.getCompressionType().toString(), file.getCompressionArgument());
            long timeEnd = System.currentTimeMillis();
            stats.setInputSize(isw.getReadBytes());
            stats.setOutputSize(osw.getWrittenBytes());
            stats.setExecutionTime(timeEnd - timeStart);
            file.setStatistics(stats);
            file.setHeaderIndex(totalStats.getOutputSize());
            totalStats.setOutputSize(totalStats.getOutputSize() + stats.getOutputSize());
            totalStats.setInputSize(totalStats.getInputSize() + stats.getInputSize());
            totalStats.setExecutionTime(totalStats.getExecutionTime() + stats.getExecutionTime());
            isw.close();
        }
        Folder[] folders = parentFolder.getFolders();
        for (Folder folder : folders) {
            traverseCompress(os, folder);
        }
    }
    
    /**
     * traverse the filetree decompressing every file
     * @param is input stream pointing a compressed file
     * @param parentFolder current parent folder of the traverse
     * @param path path to the current directory being decompressed
     * @throws Exception if io error
     */
    private void traverseDecompress(InputStream is, Folder parentFolder, String path) throws Exception {
        Archive[] files = parentFolder.getFiles();
        for (Archive file : files) {
            Statistics stats = new Statistics();
            InputStreamWatcher isw = new InputStreamWatcher(is);
            OutputStreamWatcher osw = new OutputStreamWatcher(new BufferedOutputStream(new FileOutputStream(path+System.getProperty("file.separator")+file.getFilename())));
            long timeStart = System.currentTimeMillis();
            DomainController.getInstance().chainDecompress(isw, osw, file.getCompressionType().toString());
            long timeEnd = System.currentTimeMillis();
            stats.setInputSize(isw.getReadBytes());
            stats.setOutputSize(osw.getWrittenBytes());
            stats.setExecutionTime(timeEnd - timeStart);
            file.setStatistics(stats);
            totalStats.setOutputSize(totalStats.getOutputSize() + stats.getOutputSize());
            totalStats.setInputSize(totalStats.getInputSize() + stats.getInputSize());
            totalStats.setExecutionTime(totalStats.getExecutionTime() + stats.getExecutionTime());
            osw.flush();
            osw.close();
        }
        Folder[] folders = parentFolder.getFolders();
        for (Folder folder : folders) {
            File f = new File(path+System.getProperty("file.separator")+folder.getName());
            f.mkdir();
            traverseDecompress(is, folder, f.getCanonicalPath());
        }
    }
}