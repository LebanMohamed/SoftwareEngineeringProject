package common.gui.util;

/**
 * A prompt which leads to the creation and displaying of a {@link javax.swing.JOptionPane}.
 */
public abstract class GenericPrompt {

    protected boolean successful = false;

    public abstract void show();

    public boolean isSuccessful() {
        return successful;
    }
}
