package pg.gipter.monitor.ui;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import pg.gipter.monitor.statistics.collections.Statistic;
import pg.gipter.monitor.statistics.services.StatisticService;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class MainController extends AbstractController {

    @FXML
    private AnchorPane mainAnchorPane;

    private StatisticService statisticService;

    public MainController(UILauncher uiLauncher) {
        super(uiLauncher);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        statisticService = new StatisticService();

        setProperties();
    }

    private void setProperties() {
        List<Statistic> failedTries = statisticService.getFailedTries(LocalDateTime.now().minusMonths(2));
        log.info("Found {} statistics.", failedTries.size());
    }
}
