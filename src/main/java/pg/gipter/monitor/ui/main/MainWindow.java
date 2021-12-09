package pg.gipter.monitor.ui.main;

import pg.gipter.monitor.ui.*;

/**Created by Gawa 2019-03-02*/
public class MainWindow extends AbstractWindow {

    public MainWindow(AbstractController controller) {
        super(controller);
    }

    @Override
    protected String fxmlFileName() {
        return "main.fxml";
    }

    @Override
    protected ImageFile windowImgFileName() {
        return ImageFile.MINION_TRAY;
    }

    @Override
    protected String cssFileName() {
        return "main.css";
    }

    @Override
    public String windowTitleBundle() {
        return "main.title";
    }
}
