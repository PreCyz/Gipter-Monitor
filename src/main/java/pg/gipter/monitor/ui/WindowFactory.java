package pg.gipter.monitor.ui;

import pg.gipter.monitor.ui.details.DetailsController;
import pg.gipter.monitor.ui.details.DetailsWindow;
import pg.gipter.monitor.ui.main.MainController;
import pg.gipter.monitor.ui.main.MainWindow;

/**Created by Gawa 2017-10-04*/
public enum WindowFactory {
    MAIN {
        @Override
        public AbstractWindow createWindow(UILauncher uiLauncher) {
            return new MainWindow(new MainController(uiLauncher));
        }
    },
    DETAILS {
        @Override
        public AbstractWindow createWindow(UILauncher uiLauncher) {
            return new DetailsWindow(new DetailsController(uiLauncher));
        }
    };

    public abstract AbstractWindow createWindow(UILauncher uiLauncher);
}
