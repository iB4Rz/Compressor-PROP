package Presentation;

import java.awt.*;

import javax.swing.*;

public class ShowImage extends JPanel {
    /**
     * Byte array with the information composition of the image 
     * where the first 4 bytes indicates the width and the next 4 bytes indicates the height
     */
    private byte[] byte_array;
    /**
     * Image width
     */
    protected int width = 0;
    /**
     * Height width
     */
    protected int height = 0;
    /**
     * The frame to place the panel
     */
    private JFrame frame;

    /**
     * Panel display image constructor
     * @param path path from the image
     * @param frame frame to place the panel to show the image
     * @param lossy true to have the image after lossy compression, false the original
     */
    public ShowImage(String path, JFrame frame, boolean lossy) {
        this.frame = frame;
        try {
            if (lossy)  byte_array = PresentationController.getInstance().getImageAfterLossyCompression(path);
            else byte_array = PresentationController.getInstance().getImage(path);
            width = byte_array[3] & 0xFF | (byte_array[2] & 0xFF) << 8 | (byte_array[1] & 0xFF) << 16 | (byte_array[0] & 0xFF) << 24;
            height = byte_array[7] & 0xFF | (byte_array[6] & 0xFF) << 8 | (byte_array[5] & 0xFF) << 16 | (byte_array[4] & 0xFF) << 24;
            frame.setSize(width, height);
        } catch (Exception e) {
            System.out.println("Unreachable Folder/File");
        }
        setLayout(new BorderLayout());
    }

    /**
     * Paint the image pixel by pixel in the respective panel frame
     * @param g grafics tool to paint
     */
    @Override
    public void paint(Graphics g) {
        super.paintComponent(g);

        int i = 0;
        int j = 0;

        for (int x = 8; x < byte_array.length; x+=3) {
            if(i >= width) { i = 0; ++j; }

            int red = (int)(byte_array[x] & 0x0FF);
            int green = (int)(byte_array[x+1] & 0x0FF);
            int blue = (int)(byte_array[x+2] & 0x0FF);

            Color pixelColor = new Color (red,green,blue);
            
            g.setColor(pixelColor);
            g.drawLine(i, j, i, j);
            ++i; 
        }   
    }
}
