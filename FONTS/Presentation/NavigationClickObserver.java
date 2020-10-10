package Presentation;

interface NavigationClickObserver {
    /**
     * Used when a single file clik action occurs.
     * @param context file path selected
     */
    public void SingleClick_File(String context);

    /**
     * Used when a single folder clik action occurs.
     * @param context folder path selected
     */
    public void SingleClick_Folder(String context);
}