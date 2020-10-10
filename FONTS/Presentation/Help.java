package Presentation;

public class Help {
    /**
     * User manual
     */
    private static String Help =
        "<html>"+
        "<body>"+
        "<h1>Welcome to the user manual!</h1>"+
        "<p>In this manual we will offer you a detailed explanation of the program opearation so that you can use all the functionalities of our product without any problem.</p>"+
        "<p></p>"+
        "<h2>1.WORK ENVIRONMENT</h2>"+
        "<p>Before starting with the description of the functionalities we will specify the main components of the work environment.</p>"+
        "<p>The environment we work on is divided into three main parts:</p>"+
        "<p></p>"+
        "<b>1.1.Toolbar</b>"+
        "<p>Where the buttons that will be used to carry out the main functionalities will appear.</p>"+
        "<p></p>"+
        "<b>1.2.Properties panel</b>"+
        "<p>Where certain properties relative to the selected file will appear. This panel will appear only when an individual file (not a folder) has been selected in the navigation panel.</p>"+
        "<p></p>"+
        "<b>1.3.Navigation panel</b>"+
        "<p>Where the file from which the properties will appear in the properties panel will be chosen.</p>"+
        "<p></p>"+
        "<p>Apart from these main panels, as the different functionalities of the program are explored, the environment also presents different pop-up windows. These are:</p>"+
        "<p></p>"+
        "<b>1.4.Open file / folder window</b>"+
        "<p>Where you can explore the hierarchy of system files and select the file / folder to be compressed / decompressed. This window will appear when you press the 'Open' button.</p>"+
        "<p></p>"+
        "<b>1.5.Save window</b>"+
        "<p>Where you can choose the location where the compressed / decompressed file will be saved. This window will appear when you press the 'Compress' button if the selected file is a non-compressed file and when you press the 'Decompress' button if the selected file is a previously compressed file.</p>"+
        "<p></p>"+
        "<b>1.6.Help window</b>"+
        "<p>Where you can consult a brief help manual. This window will appear when you press the 'Help' button.</p>"+
        "<p></p>"+
        "<b>1.7.Visualisation window</b>"+
        "<p>Where we can preview the individual files (not folders) selected in the navigation window. This window will appear when you press the 'Display' button or if the selected file is a .ppm image when you press the 'Lossy' button.</p>"+
        "<p></p>"+
        "<p></p>"+
        "<h2>2.MAIN FUNCTIONALITIES</h2>"+
        "<b>2.1.Compress file / folder</b>"+
        "<p>To perform the compression of a particular file we must follow the following steps:</p>"+
        "<ul><li>Press the 'Open' button on the toolbar.</li><li>Select the file / folder from the pop-up window that we want to compress (other than a previously compressed file).</li><li>(Follow the next step only if we want to choose the compression algorithm and dictionary size for a given file!) Select the target file in the navigation window. If the file is not an image, select the algorithm with which the compression will be performed by clicking on the drop-down 'Algorithm'. If the selected algorithm is not the LZSS, select the size of the dictionary by clicking on the 'Parameter' drop-down.</li><li>Press the 'Compress' button on the toolbar.</li><li>Choose the place in the set of system files where the compressed file / folder will be stored and with what name it will. Once the browser's 'Save' button has been pressed, compression will begin.</li></ul>"+
        "<p></p>"+
        "<b>2.2.Decompress file / folder</b>"+
        "<p>To decompress a particular file we must follow the following steps:</p>"+
        "<ul><li>Press the 'Open' button on the toolbar.</li><li>Select the file / folder from the pop-up window to open the file / folder you want to decompress (it has to be a previously compressed file / folder).</li><li>Press the 'Decompress' button on the toolbar.</li><li>Choose the place in the set of system files where the decompressed file / folder will be saved. Once the browser's 'Save' button has been pressed, decompression will begin.</li></ul>"+
        "<p>Attention! If a compressed file / folder is opened, it will appear in the navigation panel with the original name and not with the name we have given it when we have saved it when doing the compression.</p>"+
        "<p></p>"+
        "<b>2.3.Statistics display</b>"+
        "<p>To visualize the compression performance of a file / folder through statistics, we must follow these steps:</p>"+
        "<ul><li>Compress / decompress the file / folder as explained in points 2.1 and 2.2.</li><li>Press the 'Statistics' button to display the total statistics.</li><li>If we have compressed / decompressed a folder select each file to display the statistics at the individual level in the properties window. (If we have compressed / decompressed an individual file and selected it, the same statistics will appear in the properties window as by pressing the 'Statistics' button.</li></ul>"+
        "<p>The statistics shown will contain the following information:</p>"+
        "<ul><li>Compression/decompression ratio: It results from the division of the size of the original file / folder by the size of the compressed / decompressed file / folder.</li><li>Space savings: It is defined as the size reduction in relation to the uncompressed size.</li><li>Read: Refers to the size of data read when performing compression / decompression.</li><li>Written: Refers to the size of written data when performing compression / decompression.</li><li>Elapsed time: Refers to the elapsed time while compressing / decompressing.</li><li>Compression/decompression per second/Speed: It results from the division of the size of the compressed / decompressed file / folder by the elapsed time.</li></ul>"+
        "<p></p>"+
        "<b>2.4.File preview</b>"+
        "<p>To be able to view an individual file (not a folder) in the viewing window without leaving the program we must follow the following steps:</p>"+
        "<ul><li>Open the file to be displayed so that it appears in the navigation window.</li><li>Select the file to be displayed in the navigation window.</li><li>Press the 'Display' button to display the file in the viewing window. If the selected file is an uncompressed image, the 'Lossy' button will also appear, which, when pressed, will show us the resulting image after compression and decompression with the parameter selected in the 'Parameter' drop-down.</li></ul>"+
        "<p></p>"+
        "<b>2.5.Help</b>"+
        "<p>If you have any questions regarding the use of the program, you can consult a help manual integrated in the interface itself by clicking on the 'Help' button on the toolbar.</p>"+
        "<p></p>"+
        "<p></p>"+
        "<p></p>"+
        "<p></p>"+
        "<p></p>"+
        "</body>"+
        "</html>";

    /**
     * @return the help
     */
    public static String getHelp() {
        return Help;
    }
}
