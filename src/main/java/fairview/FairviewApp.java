package main.java.fairview;
import main.java.fairview.gui.FairviewGUI;
import main.java.fairview.gui.FairviewLookAndFeel;
import main.java.fairview.users.ConferenceManager;

public class FairviewApp {
    public static void main(String[] args) {
        FairviewLookAndFeel.apply();
        ConferenceManager manager = new ConferenceManager("Manager", "ConferenceOrg");

        javax.swing.SwingUtilities.invokeLater(() -> {
            FairviewGUI gui = new FairviewGUI(manager);
            gui.setVisible(true);
        });
    }
}