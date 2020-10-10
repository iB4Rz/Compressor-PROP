import Presentation.PresentationController;
public class MainDriver {
    public static void main(String[] args) {
        try {
            PresentationController.getInstance().DisplayUI();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
