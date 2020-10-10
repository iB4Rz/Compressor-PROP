package Presentation;

import java.awt.*;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class MainFrame extends JFrame {
    /**
     * Navigation panel
     */
    private final NavigationPanel navigation;
    /**
     * toolbar panel
     */
    private final Toolbar toolbar;
    /**
     * Compression properties panel
     */
    private FormPanel formPanel;

    /**
     * Main and default frame constructor 
     */
    public MainFrame() {

        setTitle("El Compressor");

        setLayout(new BorderLayout());

        toolbar = new Toolbar();
        navigation = new NavigationPanel();
        formPanel = new FormPanel();
        PresentationController.getInstance().setNavigator(navigation);
        navigation.subscribeClickFile(formPanel); // receive file selection signals
        navigation.subscribeClickFolder(formPanel); // receive folder selection signals

        add(formPanel, BorderLayout.EAST);
        add(toolbar, BorderLayout.NORTH);
        add(navigation, BorderLayout.CENTER);
        
        setSize(600, 500);
        setLocationRelativeTo(null);    //centering frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}