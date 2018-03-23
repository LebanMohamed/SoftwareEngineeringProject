package common;

import common.gui.LoginFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Application entry-point, creates and shows the {@link LoginFrame}.
 */
public final class Main {

    public static void main(String[] args) {
        // Apply global frame styles
        applyStyles();

        // Open LoginFrame
        LoginFrame frame = new LoginFrame();
        frame.setVisible(true);
    }

    /**
     * Sets the look and feel, as well as other global graphical themes.
     */
    private static void applyStyles() {
        // Get os name
        String osName = System.getProperty("os.name");

        // Set UI look and feel based on OS name
        try {
            if (osName.startsWith("Windows"))
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            else
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ex) {
            // Unable to set look and feel, the default will be used
            System.err.println("Unable to load desired look and feel, the default one for this platform will be used.");
        }

        // Set alternate table row color
        UIDefaults defaults = UIManager.getLookAndFeelDefaults();

        if (defaults.get("Table.alternateRowColor") == null)
            defaults.put("Table.alternateRowColor", new Color(235, 255, 255));
    }
}