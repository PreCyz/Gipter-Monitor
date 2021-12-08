package pg.gipter.monitor.ui;

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
import pg.gipter.monitor.ui.dto.ActiveSupportDetails;

import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainController extends AbstractController {

    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private Button getStatisticsButton;
    @FXML
    private Label totalLabel;
    @FXML
    private TableView<ActiveSupportDetails> tableView;

    private StatisticService statisticService;

    public MainController(UILauncher uiLauncher) {
        super(uiLauncher);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        statisticService = new StatisticService();

        setColumns();
        setProperties();
    }

    private void setColumns() {
        int columnIdx = 0;
        TableColumn<ActiveSupportDetails, ?> column;
        /*TableColumn<ActiveSupportDetails, ?> column = tableView.getColumns().get(columnIdx++);
        TableColumn<ActiveSupportDetails, String> statisticIdColumn = new TableColumn<>();
        statisticIdColumn.setText(column.getText());
        statisticIdColumn.setPrefWidth(column.getPrefWidth());
        statisticIdColumn.setCellValueFactory(new PropertyValueFactory<>("statisticId"));
        statisticIdColumn.setEditable(false);*/

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

        /*column = tableView.getColumns().get(columnIdx++);
        TableColumn<ActiveSupportDetails, UploadStatus> lastUpdateStatusColumn = new TableColumn<>();
        lastUpdateStatusColumn.setText(column.getText());
        lastUpdateStatusColumn.setPrefWidth(column.getPrefWidth());
        lastUpdateStatusColumn.setCellValueFactory(new PropertyValueFactory<>("lastUpdateStatus"));
        lastUpdateStatusColumn.setCellFactory(TextFieldTableCell.forTableColumn(getUploadStatusConverter()));
        lastUpdateStatusColumn.setEditable(false);*/




        /*column = tableView.getColumns().get(columnIdx++);
        TableColumn<ActiveSupportDetails, String> errorMsgColumn = new TableColumn<>();
        errorMsgColumn.setText(column.getText());
        errorMsgColumn.setPrefWidth(column.getPrefWidth());
        errorMsgColumn.setCellValueFactory(new PropertyValueFactory<>("errorMsg"));
        errorMsgColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        errorMsgColumn.setEditable(false);*/


        column = tableView.getColumns().get(columnIdx++);
        TableColumn<ActiveSupportDetails, LocalDateTime> errorDateColumn = new TableColumn<>();
        errorDateColumn.setText(column.getText());
        errorDateColumn.setPrefWidth(column.getPrefWidth());
        errorDateColumn.setCellValueFactory(new PropertyValueFactory<>("errorDate"));
        errorDateColumn.setCellFactory(TextFieldTableCell.forTableColumn(getLocalDateTimeConverter()));
        errorDateColumn.setEditable(false);

        column = tableView.getColumns().get(columnIdx++);
        TableColumn<ActiveSupportDetails, String> causeColumn = new TableColumn<>();
        causeColumn.setText(column.getText());
        causeColumn.setPrefWidth(column.getPrefWidth());
        causeColumn.setCellValueFactory(new PropertyValueFactory<>("cause"));
        causeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        causeColumn.setEditable(false);

/*
        column = tableView.getColumns().get(columnIdx++);
        TableColumn<ActiveSupportDetails, Boolean> processedColumn = new TableColumn<>();
        processedColumn.setText(column.getText());
        processedColumn.setPrefWidth(column.getPrefWidth());
        processedColumn.setCellValueFactory(new PropertyValueFactory<>("processed"));
        processedColumn.setCellFactory(TextFieldTableCell.forTableColumn(getBooleanConverter()));
        processedColumn.setEditable(true);

        column = tableView.getColumns().get(columnIdx++);
        TableColumn<ActiveSupportDetails, LocalDateTime> processedDateTimeColumn = new TableColumn<>();
        processedDateTimeColumn.setText(column.getText());
        processedDateTimeColumn.setPrefWidth(column.getPrefWidth());
        processedDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("processedDateTime"));
        processedDateTimeColumn.setCellFactory(TextFieldTableCell.forTableColumn(getLocalDateTimeConverter()));
        processedDateTimeColumn.setEditable(false);

        column = tableView.getColumns().get(columnIdx++);
        TableColumn<ActiveSupportDetails, String> userProcessorColumn = new TableColumn<>();
        userProcessorColumn.setText(column.getText());
        userProcessorColumn.setPrefWidth(column.getPrefWidth());
        userProcessorColumn.setCellValueFactory(new PropertyValueFactory<>("userProcessor"));
        userProcessorColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userProcessorColumn.setEditable(true);

        column = tableView.getColumns().get(columnIdx++);
        TableColumn<ActiveSupportDetails, String> commentColumn = new TableColumn<>();
        commentColumn.setText(column.getText());
        commentColumn.setPrefWidth(column.getPrefWidth());
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        commentColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        commentColumn.setEditable(true);*/

        tableView.getColumns().clear();
        tableView.getColumns().addAll(
                /*statisticIdColumn,*/
                username,
                applicationVersionColumn,
                javaVersionColumn,
                controlSystemColumn,
                lastRunTypeColumn,
                lastSuccessDateColumn,
                lastFailedDateColumn,
                /*lastUpdateStatusColumn,*/
                /*errorMsgColumn,*/
                errorDateColumn,
                causeColumn/*,
                processedColumn,
                processedDateTimeColumn,
                userProcessorColumn,
                commentColumn*/
        );

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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
            LocalDateTime from = LocalDateTime.of(fromDatePicker.getValue(), LocalTime.now());
            List<ActiveSupportDetails> failedTries = statisticService.getFailedTries(from);
            log.info("Found {} statistics.", failedTries.size());
            analyze(failedTries);
            tableView.setItems(FXCollections.observableList(failedTries));
        };
    }

    private void analyze(List<ActiveSupportDetails> result) {
        long diffCouldNotBeProduced = result.stream().filter(asd -> asd.getCause().startsWith("Diff could not be produced")).count();
        long forGivenRepositories = result.stream().filter(asd -> asd.getCause().startsWith("For given repositories")).count();
        long other = result.size() - diffCouldNotBeProduced - forGivenRepositories;
        String labelText = String.format("Diff could not be produced: %d%nFor given repositories: %d%nOther: %d%nTotal: %d",
                diffCouldNotBeProduced,
                forGivenRepositories,
                other,
                result.size()
        );
        totalLabel.setText(labelText);
    }
}
