package pg.gipter.monitor.ui;

import pg.gipter.monitor.ui.main.MainController;
import pg.gipter.monitor.ui.main.MainWindow;

/**Created by Gawa 2017-10-04*/
public enum WindowFactory {
    MAIN {
        @Override
        public AbstractWindow createWindow(UILauncher uiLauncher) {
            return new MainWindow(new MainController(uiLauncher));
        }
    };

    public abstract AbstractWindow createWindow(UILauncher uiLauncher);
}
