package pg.gipter.monitor.ui;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController extends AbstractController {

    @FXML
    private AnchorPane mainAnchorPane;

    public MainController(UILauncher uiLauncher) {
        super(uiLauncher);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        setProperties();
    }

    private void setProperties() {
    }
}
