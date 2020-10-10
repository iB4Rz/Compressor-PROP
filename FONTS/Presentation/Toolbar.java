package Presentation;

import Presentation.PresentationController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Toolbar extends JPanel implements ActionListener {    
    /**
     * File button
     */
    private final JButton fileButton;
    /**
     * Compress button
     */
    private final JButton compressButton;
    /**
     * Total statistics button
     */
    private final JButton StatsButton;
    /**
     * Help button, display user manual
     */
    private final JButton HelpButton;
    /**
     * File chooser
     */
    private final JFileChooser fileChooser;
    /**
     * Compressed save file chooser
     */
    private final JFileChooser compressedSaveChooser;
    /**
     * Decompressed save file chooser
     */
    private final JFileChooser decompressedSaveChooser;
    /**
     * Indicates if it is compressed
     */
    private boolean compressed;

    /**
     * Default toolbar panel constructor 
     * creates a superior panel with  the open, compressed/decompressed, statics, help manual buttons
     */
    public Toolbar() {
        setBorder(BorderFactory.createEtchedBorder());
        // Inicializations
        fileButton = new JButton("Open");
        compressButton = new JButton("Compress/Decompress");
        StatsButton = new JButton("Statistics");
        HelpButton = new JButton("Help");
        compressButton.setVisible(false);
        StatsButton.setVisible(false);

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        compressedSaveChooser = new JFileChooser();
        compressedSaveChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        compressedSaveChooser.setDialogTitle("Save As");
        compressedSaveChooser.setApproveButtonText("Save");

        decompressedSaveChooser = new JFileChooser();
        decompressedSaveChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        decompressedSaveChooser.setDialogTitle("Save Into");
        decompressedSaveChooser.setApproveButtonText("Save");

        // Listeners
        compressButton.addActionListener(this);
        fileButton.addActionListener(this);
        StatsButton.addActionListener(this);
        HelpButton.addActionListener(this);

        setLayout(new FlowLayout(FlowLayout.LEFT));

        add(fileButton);
        add(compressButton);
        add(StatsButton);
        add(HelpButton);
    }

    /**
     * Invoked when the open, compress, stats, help button action occurs
     * @param e action event
     */
    public void actionPerformed(final ActionEvent e) {
        final JButton clicked = (JButton) e.getSource();

        if (clicked == fileButton) {
            if (fileChooser.showOpenDialog(Toolbar.this) == JFileChooser.APPROVE_OPTION) {
                File f = fileChooser.getSelectedFile();
                try {
                    compressed = PresentationController.getInstance().readFileTree(f.getCanonicalPath());
                    if (compressed)
                        compressButton.setText("Decompress");
                    else {
                        compressButton.setText("Compress");
                    }
                    StatsButton.setVisible(false);
                    compressButton.setVisible(true);
                } catch (Exception exc) {
                    System.out.println(exc.getMessage());
                }
            }
        } else if (clicked == compressButton) {
            if (compressed) {
                if (decompressedSaveChooser.showOpenDialog(Toolbar.this) == JFileChooser.APPROVE_OPTION) {
                    File f = decompressedSaveChooser.getSelectedFile();
                    try {
                        if (!f.exists()) {
                            f.mkdir();
                        }
                        PresentationController.getInstance().decompressTo(f.getCanonicalPath());
                        StatsButton.setVisible(true);
                    } catch (Exception exc) {
                        System.out.println(exc.getMessage());
                    }
                }
            } else {
                if (compressedSaveChooser.showOpenDialog(Toolbar.this) == JFileChooser.APPROVE_OPTION) {
                    File f = compressedSaveChooser.getSelectedFile();
                    try {
                        PresentationController.getInstance().compressTo(f.getCanonicalPath());
                        StatsButton.setVisible(true);
                    } catch (Exception exc) {
                        System.out.println(exc.getMessage());
                    }
                }
            }
        } // invisible by default, should be visible after getting valid (non zero) total stats
        else if (clicked == StatsButton) { 
            JFrame frame = new JFrame();
            frame.setSize(535, 155);
            frame.setTitle("Total Statistics");
            frame.add(new ShowText(TotalStatistics.getStats()));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

        } else if (clicked == HelpButton) {
            JFrame frame = new JFrame();
            frame.setTitle("Help");
            frame.setSize(600, 850);
            frame.add(new ShowText(Help.getHelp(), "text/html"));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }
}
