package Persistence;

import java.util.Vector;
import java.util.regex.Pattern;
import java.nio.file.Paths;

public class Folder {
    /**
     * Vector of files representing the files in this folder
     */
    private Vector<Archive> files = new Vector<Archive>();
    /**
     * Vector of folders representing the folders in this folder
     */
    private Vector<Folder> folders = new Vector<Folder>();
    /**
     * name of this folder
     */
    private String name;
    /**
     * parent folder or null if this folder represents the root of a hierarchy
     */
    private final Folder parent;
    /**
     * root instance of the hierarchy this folder is member
     */
    private Folder root = null;

    /**
     * Constructor
     * @param name name of the created folder
     * @param parent instance of the parent folder of the created folder or null if its the root
     */
    Folder(String name, Folder parent) {
        this.name = name;
        this.parent = parent;
        if (parent == null)
            root = this;
        else {
            root = parent.root;
            parent.folders.add(this);
        }
    }

    /**
     * Folder parent getter
     * @return instance of the parent folder
     */
    public Folder getParent() {
        return parent;
    }

    /**
     * Root folder getter
     * @return an intance of the centinel root folder
     */
    public Folder getRoot() {
        return root;
    }

    /**
     * Folder name getter
     * @return the name of this folder
     */
    public String getName() {
        return name;
    }

    /**
     * Add a file into this folder
     * @param file valid instance of a file
     */
    public void addFile(Archive file) {
        files.add(file);
    }

    /**
     * Files getter
     * @return an array containing all the files in this folder
     */
    public Archive[] getFiles() {
        Archive[] aux = new Archive[files.size()];
        return files.toArray(aux);
    }

    /**
     * Folders getter
     * @return array of folders containing all the sub folders of this folder
     */
    public Folder[] getFolders() {
        Folder[] aux = new Folder[folders.size()];
        return folders.toArray(aux);
    }

    /**
     * Filename getter
     * @return String[] containing all filenames of this folder
     */
    public String[] getFileNames() {
        String[] aux = new String[files.size()];
        for (int i = 0; i < files.size(); ++i) {
            aux[i] = files.get(i).getFilename();
        }
        return aux;
    }

    /**
     * Folder names getter
     * @return String[] containing all subfolder names of this folder
     */
    public String[] getFolderNames() {
        String[] aux = new String[folders.size()];
        for (int i = 0; i < folders.size(); ++i) {
            aux[i] = folders.get(i).name;
        }
        return aux;
    }

    /**
     * Folder getter
     * @param start root of the filetree that will be traversed
     * @param pathToFolder relative path to a folder from start
     * @return a folder instance
     * @throws Exception if the folder does not exist in the path or the path is invalid
     */
    public static Folder getFolder(Folder start, String pathToFolder) throws Exception {
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String steps[] = Paths.get(pathToFolder).toString().split(pattern);
        
        if (pathToFolder.equals("."))
            return start.root;
        else if (steps.length == 0) throw new Exception(pathToFolder + " folder path not valid");
        
        Folder aux = start;
        for (String step : steps) {
            Folder[] folders = aux.getFolders();
            boolean found = false;
            for (Folder folder : folders) {
                if (folder.getName().equals(step)) {
                    aux = folder;
                    found = true;
                    break;
                }
            }
            if (!found)
                throw new Exception("error traversing " + pathToFolder + " , " + step + " not found");
        }
        return aux;
    }

    /**
     * File getter
     * @param start root of the filetree that will be traversed
     * @param pathToFile relative path to an Archive from start
     * @return Archive instance
     * @throws Exception if the Archive does not exist in the path or the path is invalid
     */
    public static Archive getFile(Folder start, String pathToFile) throws Exception {
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String steps[] = Paths.get(pathToFile).toString().split(pattern);
        
        if (steps.length == 0) throw new Exception(pathToFile + " filepath not valid");
        
        Folder aux = start;
        int end = steps.length - 1;
        for (int i = 0; i < end; ++i) {
            Folder[] folders = aux.getFolders();
            boolean found = false;
            for (Folder folder : folders) {
                if (folder.getName().equals(steps[i])) {
                    aux = folder;
                    found = true;
                    break;
                }
            }
            if (!found)
                throw new Exception("error traversing " + pathToFile + " , " + steps[i] + " not found");
        }
        Archive[] files = aux.getFiles();
        for (Archive file : files)
            if (file.getFilename().equals(steps[end]))
                return file;
        throw new Exception(steps[end] + " does not exist at " + pathToFile);
    }

    /**
     * Gets the path from the root to this folder
     * @return String[] of folder names representing a path
     */
    public String[] getPath() {
        Vector<String> v = new Vector<String>();
        Folder aux = this;
        while (aux != root) {
            v.add(0, aux.getName());
            aux = aux.parent;
        }
        String[] result = new String[v.size()];
        v.toArray(result);
        return result;
    }
}