import fairview.gui.FairviewGUI;
import fairview.users.ConferenceManager;

public class FairviewApp {
    public static void main(String[] args) {
        ConferenceManager manager = new ConferenceManager("Manager", "ConferenceOrg");

        javax.swing.SwingUtilities.invokeLater(() -> {
            FairviewGUI gui = new FairviewGUI(manager);
            gui.setVisible(true);
        });
    }
}