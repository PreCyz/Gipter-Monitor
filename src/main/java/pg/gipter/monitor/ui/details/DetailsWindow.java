package pg.gipter.monitor.ui.details;

import pg.gipter.monitor.ui.*;

public class DetailsWindow extends AbstractWindow {

    public DetailsWindow(AbstractController controller) {
        super(controller);
    }

    @Override
    protected String fxmlFileName() {
        return "details.fxml";
    }

    @Override
    protected ImageFile windowImgFileName() {
        return ImageFile.MINION_TRAY;
    }

    @Override
    protected String cssFileName() {
        return "details.css";
    }

    @Override
    public String windowTitleBundle() {
        return "details.title";
    }
}
