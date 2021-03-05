package net.kemitix.clover.ui.fragment.settings;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import net.kemitix.clover.ui.AbstractView;

public class SettingsView
        extends AbstractView<SettingsController, SettingsView> {
    @Override
    public Parent getRoot() {
        return new VBox(
                new Label(
                        "Settings"
                )
        );
    }
}
