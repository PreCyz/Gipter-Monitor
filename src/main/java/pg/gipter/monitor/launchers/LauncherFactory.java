package pg.gipter.monitor.launchers;

import javafx.stage.Stage;
import pg.gipter.monitor.ui.UILauncher;

public class LauncherFactory {
    private LauncherFactory() {}

    public static Launcher getLauncher(Stage stage) {
        return new UILauncher(stage);
    }

}
