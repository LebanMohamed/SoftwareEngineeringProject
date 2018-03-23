package common.gui.util;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * A set of helper methods for {@link javax.swing.JTextField}.
 */
public final class TextFieldHelper {

    /**
     * Adds an {@link java.awt.event.KeyListener} to the provided {@link JTextField} to disallow non-digit input.
     * Source: https://stackoverflow.com/questions/1313390/is-there-any-way-to-accept-only-numeric-values-in-a-jtextfield
     */
    public static void disallowNonDigitInput(JTextField tf) {
        tf.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();

                if (c > 127) // disallow non ascii input
                    e.consume();
                if (c == 9) // disallow tab
                    e.consume();
                if (c >= 33 && c <= 47 || c >= 58 && c <= 126)
                    e.consume();
                super.keyTyped(e);
            }
        });
    }

    /**
     * Adds an {@link java.awt.event.KeyListener} to the provided {@link JTextField} to disallow non-digit input.
     * It will also accept . input.
     */
    public static void disallowNonNumericInput(JTextField tf) {
        tf.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();

                if (c > 127) // disallow non ascii input
                    e.consume();
                if (c == 9) // disallow tab
                    e.consume();
                if (c >= 33 && c <= 45 || c == 47 || c >= 58 && c <= 126)
                    e.consume();
                super.keyTyped(e);
            }
        });
    }

    /**
     * Adds an {@link java.awt.event.KeyListener} to the provided {@link JTextField} to disallow non-date input.
     * It accepts integers, spaces, dashes and colons.
     */
    public static void disallowNonDateInput(JTextField tf) {
        tf.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();

                if (c > 127) // disallow non ascii input
                    e.consume();
                if (c == 9) // disallow tab
                    e.consume();
                if (c >= 33 && c < 45 || c == 47 || c > 58 && c <= 126)
                    e.consume();
                super.keyTyped(e);
            }
        });
    }

    /**
     * Adds a default value to a {@link JTextField}, this will instantly change the value stored in the text field.
     * Source: https://docs.oracle.com/javase/tutorial/uiswing/events/focuslistener.html
     * Source: https://stackoverflow.com/questions/3953208/value-change-listener-to-jtextfield/3953219#3953219
     */
    public static void enableDefaultValue(final JTextField tf, final String defaultValue) {
        // Set current value
        tf.setText(defaultValue);
        tf.setForeground(Color.gray);

        // Add listener
        tf.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (tf.getText().equals(defaultValue)) {
                    tf.setForeground(Color.black);
                    tf.setText("");
                }
                super.focusGained(e);
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (tf.getText().equals("")) {
                    tf.setForeground(Color.gray);
                    tf.setText(defaultValue);
                }
                super.focusLost(e);
            }
        });
        tf.getDocument().addDocumentListener(new DocumentChangeListener() {
            @Override
            public void onChange(DocumentEvent e) {
                if (!tf.getText().equals(defaultValue)) {
                    tf.setForeground(Color.black);
                }
            }
        });
    }
}
