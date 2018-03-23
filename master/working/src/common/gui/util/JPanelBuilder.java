package common.gui.util;

import javax.swing.*;
import java.awt.*;

/**
 * A helper to create a {@link JPanel}.
 */
public final class JPanelBuilder {

    private final JPanel panel = new JPanel();

    public JPanelBuilder setLayout(LayoutManager layout) {
        panel.setLayout(layout);
        return this;
    }

    public JPanelBuilder add(Component component) {
        panel.add(component);
        return this;
    }

    public JPanelBuilder addLabel(String text) {
        return add(new JLabel(text));
    }

    public JPanelBuilder addEmptyLabel() {
        return add(new JLabel());
    }

    public JPanel getPanel() {
        return panel;
    }
}
