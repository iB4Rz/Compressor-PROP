package Presentation;

import java.awt.BorderLayout;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.Vector;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Font;

public class NavigationPanel extends JPanel {
    /**
     * Current path
     */
    private String currentPath;
    /**
     * File names
     */
    private String[] FileNames = new String[0];
    /**
     * Folders name
     */
    private String[] FolderNames = new String[0];
    /**
     * Return symbol
     */
    private static String returnSymbol;
    /**
     * Folder symbol
     */
    private static String folderSymbol;
    /**
     * List model
     */
    private DefaultListModel<String> model = new DefaultListModel<>();
    /**
     * Navegation file subscribers
     */
    private Vector<NavigationClickObserver> singleClickFileSubscribers = new Vector<NavigationClickObserver>();
    /**
     * Navegation folder subscribers
     */
    private Vector<NavigationClickObserver> singleClickFolderSubscribers = new Vector<NavigationClickObserver>();

    /**
     * Default navigation panel constructor 
     * creates a navigation panel to interact with folders and files
     */
    public NavigationPanel() {
        JList<String> jlist = new JList<>(model);
        folderSymbol = getCompatibleFolderSymbol(jlist.getFont()) + " ";
        returnSymbol = getCompatibleReturnSymbol(jlist.getFont()) + " ..";
        setLayout(new BorderLayout());
        setVisible(true);
        add(new JScrollPane(jlist), BorderLayout.CENTER);

        MouseListener mouseListener = new MouseAdapter() {
            /**
             * Invoked when a mouse click occurs
             * @param mouseEvent mouse event
             */
            public void mouseClicked(MouseEvent mouseEvent) {
                JList<String> theList = (JList) mouseEvent.getSource();
                if (mouseEvent.getClickCount() % 2 == 0) { // double click
                    int index = theList.locationToIndex(mouseEvent.getPoint());
                    if (index >= 0) {
                        Object obj = theList.getModel().getElementAt(index);
                        onDoubleClick(obj);
                    }
                } else if (mouseEvent.getClickCount() == 1) { // single click
                    int index = theList.locationToIndex(mouseEvent.getPoint());
                    if (index >= 0) {
                        Object obj = theList.getModel().getElementAt(index);
                        onSingleClick(obj);
                    }
                }
            }
        };
        jlist.addMouseListener(mouseListener);
    }

    /**
     * Double click action, if the target is a folder, go to the following directoriy,
     * if the target is a return symbol, go back to the previous directory
     * @param target double click objective received
     */
    private void onDoubleClick(Object target) {
        try {
            if (Arrays.asList(FolderNames).contains(target.toString())) {
                refresh(pathTraverse(target.toString().substring(folderSymbol.length())));
            } else if (returnSymbol.equals(target.toString())) {
                refresh(pathReturn(currentPath));
            }
        } catch (Exception e) {
            System.out.println("Unreachable Folder");
        }
    }

    /**
     * One click action, target selection
     * @param target single click objective received
     */
    private void onSingleClick(Object target) {
        if (Arrays.asList(FileNames).contains(target.toString())) {
            for (NavigationClickObserver si : singleClickFileSubscribers) {
                si.SingleClick_File(pathTraverse(target.toString()));
            }
        } else {
            for (NavigationClickObserver si : singleClickFolderSubscribers) {
                si.SingleClick_Folder(pathTraverse(target.toString()));
            }
        }
    }

    /**
     * Basic refreshment function of the navigation panel
     * @param Path relative path to a file
     * @throws Exception if the path file does not exist
     */
    public void refresh(String Path) throws Exception {
        currentPath = Path;
        FileNames = PresentationController.getInstance().getFileNames(Path);
        FolderNames = PresentationController.getInstance().getFolderNames(Path);
        clean();
        if (!pathReturn(Path).equals(Path)) {
            model.addElement(returnSymbol);
        }
        Arrays.sort(FileNames);
        Arrays.sort(FolderNames);
        for (int i = 0; i < FolderNames.length; ++i) {
            FolderNames[i] = folderSymbol + FolderNames[i];
            model.addElement(FolderNames[i]);
        }
        for (int i = 0; i < FileNames.length; ++i) {
            model.addElement(FileNames[i]);
        }
        for (NavigationClickObserver si : singleClickFolderSubscribers) {
            si.SingleClick_Folder(currentPath);
        }
    }

    /**
     * Refresh current path
     * @throws Exception if the file does not exist
     */
    public void refresh() throws Exception {
        refresh(currentPath);
    }

    /**
     * From an absoulte path, gets the path from the open directory
     * @param Path relative path to a file
     * @return return the path from the open directory
     */
    private String pathReturn(String Path) {
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String steps[] = Path.split(pattern);
        if (steps.length <= 1) {
            return "";
        }
        String result = "";
        for (int i = 0; i < steps.length - 2; ++i) {
            result = result + steps[i] + System.getProperty("file.separator");
        }
        return result + steps[steps.length - 2];
    }

    /**
     * Returns the path beyond the following folder
     * @param Folder a folder
     * @return the traverse path from the folder
     */
    private String pathTraverse(String Folder) {
        if (currentPath.length() == 0)
            return Folder;
        return currentPath + System.getProperty("file.separator") + Folder;
    }

    /**
     * Remove all elemnts of the file model
     */
    private void clean() {
        model.removeAllElements();
    }

    /**
     * Check and obtain the compatible folder symnbol
     * @param f the type of font
     * @return the folder symbol
     */
    private String getCompatibleFolderSymbol(Font f) {
        int[] symbols = { 0x1F5C1, 0x1F5C0, 0x1F5BF, 0x1F4C1, 0x1F4C2 };
        for (int sym : symbols) {
            if (f.canDisplay(sym)) {
                return String.valueOf(Character.toChars(sym));
            }
        }
        return "[DIR]";
    }

    /**
     * Check and obtain the compatible return symnbol
     * @param f the type of font
     * @return the return symbol
     */
    private String getCompatibleReturnSymbol(Font f) {
        int[] symbols = { 0x2B9C };
        for (int sym : symbols) {
            if (f.canDisplay(sym)) {
                return String.valueOf(Character.toChars(sym));
            }
        }
        return "<";
    }

    /**
     * Function of design pattern observer of the file
     * @param si a navigation click observer
     */
    public void subscribeClickFile(NavigationClickObserver si) {
        singleClickFileSubscribers.add(si);
    }

    /**
     * Function of design pattern observer of the folder 
     * @param si a navigation click observer
     */
    public void subscribeClickFolder(NavigationClickObserver si) {
        singleClickFolderSubscribers.add(si);
    }
}