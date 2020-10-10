package Presentation;

import java.awt.*;

import javax.swing.*;

public class ShowText extends JPanel {

    /**
     * Panel display text constructor using textArea
     * @param text file text
     */
    public ShowText(String text) {
        JTextArea textArea = new JTextArea();
        textArea.setFont(textArea.getFont().deriveFont(14.f));
        textArea.setEditable(false); 
        textArea.setFocusable(false);
        textArea.setLineWrap(false);
        textArea.setText(text);
        textArea.setCaretPosition(0);
        
        setLayout(new BorderLayout());
        JScrollPane sp = new JScrollPane(textArea);
        add(sp, BorderLayout.CENTER);
    }

    /**
     * Panel display text constructor using textPane
     * @param text file text
     * @param textType type of the text
     */
    public ShowText(String text, String textType) {
        JTextPane textPane = new JTextPane();
        textPane.setFont(textPane.getFont().deriveFont(14.f));
        textPane.setEditable(false); 
        textPane.setFocusable(false);
        textPane.setContentType(textType);
        textPane.setText(text);
        textPane.setCaretPosition(0);
        
        setLayout(new BorderLayout());
        JScrollPane sp = new JScrollPane(textPane);
        add(sp, BorderLayout.CENTER);
    }
}
