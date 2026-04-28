package fairview.gui;

import javax.swing.*;

public class FairviewLookAndFeel {

    public static void apply() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

            // Override Nimbus colours
            UIManager.put("control", FairviewTheme.BACKGROUND);
            UIManager.put("nimbusBase", FairviewTheme.PRIMARY_DARK);
            UIManager.put("nimbusBlueGrey", FairviewTheme.PRIMARY);
            UIManager.put("text", FairviewTheme.TEXT);

        } catch (Exception e) {
            System.out.println("Failed to apply theme: " + e.getMessage());
        }
    }
}