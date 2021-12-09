package pg.gipter.monitor.ui.main;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import pg.gipter.monitor.statistics.collections.RunType;
import pg.gipter.monitor.statistics.collections.UploadStatus;
import pg.gipter.monitor.statistics.services.StatisticService;
import pg.gipter.monitor.ui.AbstractController;
import pg.gipter.monitor.ui.UILauncher;
import pg.gipter.monitor.ui.fxproperties.ActiveSupportDetails;

import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public class MainController extends AbstractController {

    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private Button getStatisticsButton;
    @FXML
    private Label couldNotBeProducedLabel;
    @FXML
    private Label unauthorizedLabel;
    @FXML
    private Label importantLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private TableView<ActiveSupportDetails> importantTableView;
    @FXML
    private TableView<ActiveSupportDetails> diffTableView;
    @FXML
    private TableView<ActiveSupportDetails> unauthorizedTableView;

    private StatisticService statisticService;

    public MainController(UILauncher uiLauncher) {
        super(uiLauncher);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        statisticService = new StatisticService();

        setColumns(diffTableView);
        setColumns(unauthorizedTableView);
        setColumns(importantTableView);
        setProperties();
    }

    private void setColumns(TableView<ActiveSupportDetails> tableView) {
        int columnIdx = 0;
        TableColumn<ActiveSupportDetails, ?> column;

        column = tableView.getColumns().get(columnIdx++);
        TableColumn<ActiveSupportDetails, String> username = new TableColumn<>();
        username.setText(column.getText());
        username.setPrefWidth(column.getPrefWidth());
        username.setCellValueFactory(new PropertyValueFactory<>("username"));
        username.setCellFactory(TextFieldTableCell.forTableColumn());
        username.setEditable(false);

        column = tableView.getColumns().get(columnIdx++);
        TableColumn<ActiveSupportDetails, String> applicationVersionColumn = new TableColumn<>();
        applicationVersionColumn.setText(column.getText());
        applicationVersionColumn.setPrefWidth(column.getPrefWidth());
        applicationVersionColumn.setCellValueFactory(new PropertyValueFactory<>("applicationVersion"));
        applicationVersionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        applicationVersionColumn.setEditable(false);

        column = tableView.getColumns().get(columnIdx++);
        TableColumn<ActiveSupportDetails, String> javaVersionColumn = new TableColumn<>();
        javaVersionColumn.setText(column.getText());
        javaVersionColumn.setPrefWidth(column.getPrefWidth());
        javaVersionColumn.setCellValueFactory(new PropertyValueFactory<>("javaVersion"));
        javaVersionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        javaVersionColumn.setEditable(false);

        column = tableView.getColumns().get(columnIdx++);
        TableColumn<ActiveSupportDetails, Map> controlSystemColumn = new TableColumn<>();
        controlSystemColumn.setText(column.getText());
        controlSystemColumn.setPrefWidth(column.getPrefWidth());
        controlSystemColumn.setCellValueFactory(new PropertyValueFactory<>("controlSystemMap"));
        controlSystemColumn.setCellFactory(TextFieldTableCell.forTableColumn(getControlSystemConverter()));
        controlSystemColumn.setEditable(false);

        column = tableView.getColumns().get(columnIdx++);
        TableColumn<ActiveSupportDetails, RunType> lastRunTypeColumn = new TableColumn<>();
        lastRunTypeColumn.setText(column.getText());
        lastRunTypeColumn.setPrefWidth(column.getPrefWidth());
        lastRunTypeColumn.setCellValueFactory(new PropertyValueFactory<>("lastRunType"));
        lastRunTypeColumn.setCellFactory(TextFieldTableCell.forTableColumn(getRunTypeConverter()));
        lastRunTypeColumn.setEditable(false);

        column = tableView.getColumns().get(columnIdx++);
        TableColumn<ActiveSupportDetails, LocalDateTime> lastSuccessDateColumn = new TableColumn<>();
        lastSuccessDateColumn.setText(column.getText());
        lastSuccessDateColumn.setPrefWidth(column.getPrefWidth());
        lastSuccessDateColumn.setCellValueFactory(new PropertyValueFactory<>("lastSuccessDate"));
        lastSuccessDateColumn.setCellFactory(TextFieldTableCell.forTableColumn(getLocalDateTimeConverter()));
        lastSuccessDateColumn.setEditable(false);

        column = tableView.getColumns().get(columnIdx++);
        TableColumn<ActiveSupportDetails, LocalDateTime> lastFailedDateColumn = new TableColumn<>();
        lastFailedDateColumn.setText(column.getText());
        lastFailedDateColumn.setPrefWidth(column.getPrefWidth());
        lastFailedDateColumn.setCellValueFactory(new PropertyValueFactory<>("lastFailedDate"));
        lastFailedDateColumn.setCellFactory(TextFieldTableCell.forTableColumn(getLocalDateTimeConverter()));
        lastFailedDateColumn.setEditable(false);

        column = tableView.getColumns().get(columnIdx++);
        TableColumn<ActiveSupportDetails, LocalDateTime> errorDateColumn = new TableColumn<>();
        errorDateColumn.setText(column.getText());
        errorDateColumn.setPrefWidth(column.getPrefWidth());
        errorDateColumn.setCellValueFactory(new PropertyValueFactory<>("errorDate"));
        errorDateColumn.setCellFactory(TextFieldTableCell.forTableColumn(getLocalDateTimeConverter()));
        errorDateColumn.setEditable(false);

        column = tableView.getColumns().get(columnIdx);
        TableColumn<ActiveSupportDetails, String> causeColumn = new TableColumn<>();
        causeColumn.setText(column.getText());
        causeColumn.setPrefWidth(column.getPrefWidth());
        causeColumn.setCellValueFactory(new PropertyValueFactory<>("cause"));
        causeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        causeColumn.setEditable(false);

        tableView.getColumns().clear();
        tableView.getColumns().addAll(
                username,
                applicationVersionColumn,
                javaVersionColumn,
                controlSystemColumn,
                lastRunTypeColumn,
                lastSuccessDateColumn,
                lastFailedDateColumn,
                errorDateColumn,
                causeColumn
        );

        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private StringConverter<Boolean> getBooleanConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(Boolean object) {
                return object.toString();
            }

            @Override
            public Boolean fromString(String string) {
                return Boolean.getBoolean(string);
            }
        };
    }

    private StringConverter<Map> getControlSystemConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(Map object) {
                return object.keySet().toString().replace("[", "").replace("]", "");
            }

            @Override
            public Map fromString(String string) {
                return Collections.emptyMap();
            }
        };
    }

    private StringConverter<RunType> getRunTypeConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(RunType object) {
                return object.name();
            }

            @Override
            public RunType fromString(String string) {
                return RunType.valueOf(string);
            }
        };
    }

    private StringConverter<UploadStatus> getUploadStatusConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(UploadStatus object) {
                return object.name();
            }

            @Override
            public UploadStatus fromString(String string) {
                return UploadStatus.valueOf(string);
            }
        };
    }

    private StringConverter<LocalDateTime> getLocalDateTimeConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(LocalDateTime object) {
                if (object == null) {
                    return "";
                }
                return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(object);
            }

            @Override
            public LocalDateTime fromString(String string) {
                if (string.isEmpty()) {
                    return null;
                }
                return LocalDateTime.from(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").parse(string));
            }
        };
    }

    private void setProperties() {
        fromDatePicker.setValue(LocalDate.now().minusMonths(1));
        getStatisticsButton.setOnAction(getStatisticsEventHandler());

    }

    private EventHandler<ActionEvent> getStatisticsEventHandler() {
        return event -> {
            getStatisticsButton.setDisable(true);
            Platform.runLater(() -> {
                LocalDateTime from = LocalDateTime.of(fromDatePicker.getValue(), LocalTime.now());
                List<ActiveSupportDetails> failedTries = statisticService.getFailedTries(from);
                log.info("Found {} statistics.", failedTries.size());
                groupByFilters(failedTries);
                getStatisticsButton.setDisable(false);
            });
        };
    }

    private void groupByFilters(List<ActiveSupportDetails> result) {
        List<ActiveSupportDetails> diffCouldNotBeProduced = result.stream()
                .filter(getFilterPredicate(Filter.DIFF).or(getFilterPredicate(Filter.REPOSITORIES)))
                .collect(toList());
        List<ActiveSupportDetails> unauthorized = result.stream()
                .filter(getFilterPredicate(Filter.UNAUTHORIZED))
                .collect(toList());
        List<ActiveSupportDetails> important = result.stream()
                .filter(getFilterPredicate(Filter.DIFF).negate())
                .filter(getFilterPredicate(Filter.REPOSITORIES).negate())
                .filter(getFilterPredicate(Filter.UNAUTHORIZED).negate())
                .collect(toList());

        diffTableView.setItems(FXCollections.observableList(diffCouldNotBeProduced));
        unauthorizedTableView.setItems(FXCollections.observableList(unauthorized));
        importantTableView.setItems(FXCollections.observableList(important));

        couldNotBeProducedLabel.setText("Diff could not be produced: " + diffCouldNotBeProduced.size());
        unauthorizedLabel.setText("Unauthorized [401]: " + unauthorized.size());
        importantLabel.setText("Important: " + important.size());
        totalLabel.setText("Total: " + result.size());
    }

    private Predicate<ActiveSupportDetails> getFilterPredicate(final Filter filter) {
        return asd -> asd.getCause().contains(filter.value());
    }
}