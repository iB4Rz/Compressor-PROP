package Presentation;

import java.awt.*;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FormPanel extends JPanel implements ActionListener, NavigationClickObserver {
    /**
     * Algorithm title 
     */
    private JLabel titleAlgorithm;
    /**
     * Parameters title
     */
    private JLabel titleParameters;
    /**
     * File input size
     */
    private JLabel in;
    /**
     * File output size
     */
    private JLabel out;
    /**
     * File elapsed time
     */
    private JLabel time; 
    /**
     * File compression/decompression ratio
     */
    private JLabel ratio;
    /**
     * File compression/decompression per second
     */
    private JLabel speed;
    /**
     * Algorithm model
     */
    private DefaultComboBoxModel algorithmModel;
    /**
     * Parameter model
     */
    private DefaultComboBoxModel parameterModel;
    /**
     * Algorithm selector
     */
    private final JComboBox algorithmSelection;
    /**
     * Parameter selector
     */
    private final JComboBox parameterSelection;
    /**
     * Button file view 
     */
    private JButton displayButton;
    /**
     * Button after image compression view
     */
    private JButton compareButton;
    /**
     * File path
     */
    private String filePath;

    /**
     * Default panel constructor 
     * creates a panel with compression properties, listeners and inicializations for the interface
     * structured with GridBag Layout
     */
    public FormPanel() {

        setVisible(false);
                
        // Dimensions
        final Dimension dim = getPreferredSize();
        dim.width = 250;
        setPreferredSize(dim);

        // Titles inicialization
        titleParameters = new JLabel("Parameter: ");
        titleAlgorithm = new JLabel("Algorithm: ");
        in = new JLabel("-");
        out = new JLabel("-");
        time = new JLabel("-");
        ratio = new JLabel("-");
        speed = new JLabel("-");

        // Algorithm selection inicialization
        algorithmSelection = new JComboBox();
        algorithmModel = new DefaultComboBoxModel();
        algorithmSelection.setModel(algorithmModel);
        algorithmSelection.setPreferredSize(new Dimension(105,25));

        // Parameters selection inicialization
        parameterSelection = new JComboBox();
        parameterModel = new DefaultComboBoxModel();
        parameterSelection.setModel(parameterModel);
        parameterSelection.setPreferredSize(new Dimension(105,25));

        // Visualization Button
        displayButton = new JButton("Display");
        compareButton = new JButton("Lossy");

        // Listeners
        algorithmSelection.addActionListener(this);
        parameterSelection.addActionListener(this);
        displayButton.addActionListener(this);
        compareButton.addActionListener(this);

        // Layout GridBag
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Compressor properties"));
        layoutComponents();
    }

    /**
     * Invoked when the button or selector action occurs
     * @param e action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // Image/Text Viewer
        if (e.getSource() == displayButton) {
            try {
                JFrame frame = new JFrame();
                if(PresentationController.getInstance().isFileImage(filePath)) {
                    frame.setTitle("Image Viewer");
                    frame.add(new ShowImage(filePath, frame, false));
                }
                else {
                    frame.setTitle("Text Viewer");
                    frame.setSize(600, 850);
                    frame.add(new ShowText(PresentationController.getInstance().getDocument(filePath)));
                }
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (Exception exc) {
                JOptionPane.showMessageDialog(this, "Unselected File", "Error", 0);
            }
        }
        // Specific button to see the image after lossy compressing and to compare
        if (e.getSource() == compareButton) {
            try {
                JFrame frame = new JFrame();
                if (PresentationController.getInstance().isFileImage(filePath)) {
                    frame.setTitle("Lossy Image Viewer");
                    frame.add(new ShowImage(filePath, frame, true));
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                }
            } catch (Exception exc) {
                JOptionPane.showMessageDialog(this, "Unselected File", "Error", 0);
            }
        }
        // Algorithm selector
        if (e.getSource() == algorithmSelection) {
            if (algorithmSelection.getItemCount() > 0) {
                String algorithm = algorithmSelection.getSelectedItem().toString();
                try {
                    PresentationController.getInstance().setCompressionType(filePath, algorithm);
                    refreshParameterSelection(filePath);
                } catch (Exception exc) {
                    System.out.println(exc.getMessage());
                }
            }                     
        }
        // Paramatere selector
        if (e.getSource() == parameterSelection) {
            if (parameterSelection.getItemCount() > 0) {
                String algorithm = algorithmSelection.getSelectedItem().toString();
                int indexParameter = parameterSelection.getSelectedIndex();
                try {
                    String[] parameterList = PresentationController.getInstance().getValidCompressionParameters(algorithm);
                    PresentationController.getInstance().setCompressionParameter(filePath, parameterList[indexParameter]);
                    refreshParameterSelection(filePath);
                } catch (Exception exc) {
                    System.out.println(exc.getMessage());
                }
            }
        }
    }

    /**
     * Invoked when a single file clik action occurs.
     * @param path file path selected
     */
    @Override
    public void SingleClick_File(String path) {
        setVisible(true);

        filePath = path;

        // Refresh
        refreshAlgortihmSelection(path);
        refreshParameterSelection(path);

        if (PresentationController.getInstance().isCompressed()) {
            // Decompression mode
            algorithmSelection.setEnabled(false);
            parameterSelection.setVisible(false);
            titleAlgorithm.setEnabled(false);
            titleParameters.setVisible(false);
            compareButton.setVisible(false);
            // Statistics decompression
            stats(path, true);
        }
        else {
            // Compression mode
            algorithmSelection.setEnabled(true);
            titleAlgorithm.setEnabled(true);
            titleParameters.setEnabled(true);
            titleParameters.setVisible(true);
            parameterSelection.setEnabled(true);
            parameterSelection.setVisible(true);
            try {
                if (PresentationController.getInstance().isFileImage(path)) {
                    compareButton.setVisible(true);
                    // Button takes the role of original image view button 
                    displayButton.setText("Original");
                }
                else {
                    compareButton.setVisible(false);
                    displayButton.setText("Display");
                }
            } catch (Exception exc) {
                System.out.println(exc.getMessage());
            }
            // Statistics compression
            stats(path, false);
        }
    }

    /**
     * Invoked when a single folder clik action occurs.
     * @param path folder path selected
     */
    @Override
    public void SingleClick_Folder(String path) {
        setVisible(false);
        displayButton.setText("Display");
    }

    /**
     * Refresh algorithm slector
     * @param path file path selected
     */
    private void refreshAlgortihmSelection(String path) {
        algorithmSelection.removeActionListener(this);
        algorithmModel.removeAllElements();
        try {
            setSelection(algorithmSelection, algorithmModel, PresentationController.getInstance().getValidCompressionTypes(path));
            algorithmSelection.setSelectedItem(PresentationController.getInstance().getCompressionType(path));
        } catch (Exception exc) {
            System.out.println(exc.getMessage());
        }
        algorithmSelection.addActionListener(this);
    }

    /**
     * Refresh parameter selector
     * @param path file path selected
     */
    private void refreshParameterSelection(String path) {
        parameterSelection.removeActionListener(this);
        parameterModel.removeAllElements();
        try {
            String algorithm = PresentationController.getInstance().getCompressionType(path);
            // Image
            if (PresentationController.getInstance().isFileImage(path)) {
                setSelection(parameterSelection, parameterModel, PresentationController.getInstance().getValidCompressionParameters(algorithm));
                String arg = PresentationController.getInstance().getCompressionParameter(path);
                if (arg != null) {
                    parameterSelection.setSelectedItem(PresentationController.getInstance().getCompressionParameter(path));
                }
            }
            // TextFile
            else {
                setSelection(parameterSelection, parameterModel, getDictBytesToHumanLegible(PresentationController.getInstance().getValidCompressionParameters(algorithm)));
                String arg = PresentationController.getInstance().getCompressionParameter(path);
                if (arg != null) {
                    parameterSelection.setSelectedItem(getDictBytesToHumanLegible(arg));
                }
            }
            // Disable if it hasn't parameters
            if (parameterSelection.getItemCount() == 0) { 
                parameterModel.addElement("-");
                parameterSelection.setModel(parameterModel);
                parameterSelection.setEnabled(false);
                titleParameters.setEnabled(false);  
            }
            else {
                parameterSelection.setEnabled(true);
                titleParameters.setEnabled(true);
            }
        } catch (Exception exc) {
            System.out.println(exc.getMessage());
        }
        parameterSelection.addActionListener(this);
    }

    /**
     * Add the elements in the respective selector
     * @param comboBox the selector to add elements
     * @param model the model used
     * @param list elements tu put in the selector
     */
    private void setSelection(JComboBox comboBox,DefaultComboBoxModel model, String[] list) {
        for(String value : list) 
            model.addElement(value);
        comboBox.setModel(model);
    }

    /**
     * Indicates input/output size, time, compression/decompression ratio, compression/decompression per second from the statistics file
     * @param file path to do the individual statistics
     * @param isCompressed indicates the mode, true indicates decompressed mode, false indicates compressed mode 
     */
    private void stats(String file, boolean isCompressed) {
        try {
            long auxIn = PresentationController.getInstance().getFileInputSizeStat(file);
            long auxOut = PresentationController.getInstance().getFileOutputSizeStat(file);
            long auxTime = PresentationController.getInstance().getFileTimeStat(file);

            // Hasn't started
            if(auxIn+auxOut+auxTime == 0) {
                in.setText("-");
                out.setText("-");
                time.setText("-");
                speed.setText("-");
                ratio.setText("-");
            }
            else {
                in.setText(bytesToHumanLegible(auxIn, true));
                out.setText(bytesToHumanLegible(auxOut, true));
                if (auxTime < 1) {
                    auxTime = 1; // 1ms precision
                    time.setText("< 1ms");
                }
                else {
                    time.setText(milisToHumanLegible(auxTime));
                }
                if(!isCompressed) {
                    speed.setText(bytesToHumanLegible((long)((double)(auxIn)/((double)(auxTime/1000.0))), true)+"/s");
                    ratio.setText(String.format("%.2f", (double)auxIn/(double)auxOut));
                }
                else {
                        speed.setText(bytesToHumanLegible((long)((double)(auxOut)/((double)(auxTime/1000.0))), true)+"/s");
                        ratio.setText(String.format("%.2f", (double)auxOut/(double)auxIn));
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Get the size of the dictionaries from the indicated exponents (2^exponent), being legible to human
     * @param exponents indicates the 2^exponent to the dictionaries size
     * @return get the size of the dictionaries to the respective exponents in a legible way
     */
    private static String[] getDictBytesToHumanLegible(String[] exponents) {
        String[] bytes = new String[exponents.length];
        for (int i = 0; i < exponents.length; ++i) 
            bytes[i] = bytesToHumanLegible((long)Math.pow(2,Long.parseLong(exponents[i])), false);
        return bytes;
    }

    /**
     * Get the size of the dictionary from the indicated exponent (2^exponent), being legible to human
     * @param exponent indicates the 2^exponent to the dictionary size
     * @return get the size of the dictionary in a legible way
     */
    private static String getDictBytesToHumanLegible(String exponent) {
        return bytesToHumanLegible((long)Math.pow(2,Long.parseLong(exponent)), false);
    }

    /**
     * Byte converter to facilitate vision
     * @param bytes bytes to deal with
     * @param decimals parameter to decide whether to use decimals, true to use it, false otherwise
     * @return the respective bytes conversion to GB, MB, KB, B  
     */
    private static String bytesToHumanLegible(long bytes, boolean decimals) {
        String format = "";
        if (decimals) format = "%.2f";
        else format = "%.0f";

        if (bytes >= 1073741824) 
            return String.format(format, bytes/1073741824.0) + " GB";
        
        else if (bytes >= 1048576)
            return String.format(format, bytes/1048576.0) + " MB";
        
        else if (bytes >= 1024) 
            return String.format(format, bytes/1024.0) + " KB";

        else return String.valueOf(bytes) + " B";
    }

    /**
     * Milliseconds conversion to facilitate vision
     * @param ms milliseconds to deal with
     * @return the respective ms conversion to hours, minutes, seconds and milliseconds
     */
    public static String milisToHumanLegible(long ms) {
        if (ms >= 3600000) {
            return String.format("%.2f", ms/3600000.0) + " h";
        }
        else if (ms >= 60000)
            return String.format("%.2f", ms/60000.0) + " min";
        else if (ms >= 1000)
            return String.format("%.2f", ms/1000.0) + " sec";
        else
            return String.valueOf(ms) + " ms";
    }

    /**
     *  GridBag Layout structured with two columns and ten rows
     */
    private void layoutComponents() {

        setLayout(new GridBagLayout());
        final GridBagConstraints bag = new GridBagConstraints();

        // First row algorithm
        bag.weightx = 1;
        bag.weighty = 0.1;

        bag.gridx = 0;
        bag.gridy = 0;
        bag.anchor = GridBagConstraints.LINE_END;
        bag.insets = new Insets(0, 0, 0, 5);
        add(titleAlgorithm, bag);

        bag.gridx = 1;
        bag.anchor = GridBagConstraints.LINE_START;
        bag.insets = new Insets(0, 0, 0, 0);
        add(algorithmSelection, bag);

        // Second row parameter
        bag.gridx = 0;
        bag.gridy = 1;
        bag.anchor = GridBagConstraints.LINE_END;
        bag.insets = new Insets(0, 0, 0, 5);
        add(titleParameters, bag);

        bag.gridx = 1;
        bag.anchor = GridBagConstraints.LINE_START;
        bag.insets = new Insets(0, 0, 0, 0);
        add(parameterSelection, bag);

        // Third row visualization
        bag.gridx = 0;
        bag.gridy = 2;
        bag.anchor = GridBagConstraints.LINE_START;
        bag.insets = new Insets(0, 0, 0, 5);
        add(new JLabel("Visualization: "), bag);

        // Forth display buttons
        bag.gridy = 3;
        bag.fill = GridBagConstraints.HORIZONTAL;
        bag.anchor = GridBagConstraints.LINE_END;
        bag.insets = new Insets(0, 10, 0, 12);
        add(displayButton, bag);

        bag.gridx = 1;
        bag.anchor = GridBagConstraints.LINE_START;
        bag.insets = new Insets(0, 18, 0, 22);
        add(compareButton, bag);

        // Fifth row individual statistic
        bag.gridx = 0;
        bag.gridy = 4;
        bag.fill = GridBagConstraints.NONE;
        bag.anchor = GridBagConstraints.LINE_START;
        bag.insets = new Insets(0, 0, 0, 0);
        add(new JLabel("Statistics: "), bag);

        // Sixth row input size
        bag.gridx = 0;
        bag.gridy = 5;
        bag.anchor = GridBagConstraints.LINE_END;
        bag.insets = new Insets(0, 0, 0, 5);
        add(new JLabel("Read: "), bag);

        bag.gridx = 1;
        bag.anchor = GridBagConstraints.LINE_START;
        bag.insets = new Insets(0, 0, 0, 0);
        add(in, bag);

        // Seventh row output size
        bag.gridx = 0;
        bag.gridy = 6;
        bag.anchor = GridBagConstraints.LINE_END;
        bag.insets = new Insets(0, 0, 0, 5);
        add(new JLabel("Written: "), bag);

        bag.gridx = 1;
        bag.anchor = GridBagConstraints.LINE_START;
        bag.insets = new Insets(0, 0, 0, 0);
        add(out, bag);

        // Eighth row compression/decompression ratio
        bag.gridx = 0;
        bag.gridy = 7;
        bag.anchor = GridBagConstraints.LINE_END;
        bag.insets = new Insets(0, 0, 0, 5);
        add(new JLabel("Ratio: "), bag);

        bag.gridx = 1;
        bag.anchor = GridBagConstraints.LINE_START;
        bag.insets = new Insets(0, 0, 0, 0);
        add(ratio, bag);

        // Nineth row elapsed time
        bag.gridx = 0;
        bag.gridy = 8;
        bag.anchor = GridBagConstraints.LINE_END;
        bag.insets = new Insets(0, 0, 0, 5);
        add(new JLabel("Elapsed Time: "), bag);

        bag.gridx = 1;
        bag.anchor = GridBagConstraints.LINE_START;
        bag.insets = new Insets(0, 0, 0, 0);
        add(time, bag);

        // Tenth row compression/decompression per second
        bag.gridx = 0;
        bag.gridy = 9;
        bag.anchor = GridBagConstraints.LINE_END;
        bag.insets = new Insets(0, 0, 0, 5);
        add(new JLabel("Speed: "), bag);

        bag.gridx = 1;
        bag.anchor = GridBagConstraints.LINE_START;
        bag.insets = new Insets(0, 0, 0, 0);
        add(speed, bag);
    } 
}