package pg.gipter.monitor.ui.main;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.quartz.SchedulerException;
import pg.gipter.monitor.domain.activeSupports.collections.ActiveSupport;
import pg.gipter.monitor.domain.activeSupports.dto.ProcessingDetails;
import pg.gipter.monitor.domain.activeSupports.services.ActiveSupportService;
import pg.gipter.monitor.domain.statistics.collections.RunType;
import pg.gipter.monitor.domain.statistics.services.StatisticService;
import pg.gipter.monitor.services.*;
import pg.gipter.monitor.ui.AbstractController;
import pg.gipter.monitor.ui.UILauncher;
import pg.gipter.monitor.ui.fxproperties.ActiveSupportDetails;
import pg.gipter.monitor.utils.ThreadUtils;

import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class MainController extends AbstractController {

    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private Button getStatisticsButton;
    @FXML
    private Button processButton;
    @FXML
    private Label diffLabel;
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
    @FXML
    private TabPane mainTabPane;
    @FXML
    private Button exitButton;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private CheckBox autostartCheckBox;
    @FXML
    private CheckBox runSchedulerCheckBox;
    @FXML
    private ComboBox<Crons> cronComboBox;

    private final StatisticService statisticService;
    private final StartupService startupService;
    private final JobService jobService;

    private SimpleObjectProperty<ActiveSupportDetails> selectedValue;
    private SimpleStringProperty diffStringProperty;
    private SimpleStringProperty unauthorizedStringProperty;
    private SimpleStringProperty importantStringProperty;
    private SimpleStringProperty totalStringProperty;

    private static List<ActiveSupportDetails> failedTries;

    private enum Summary {
        TOTAL("Total: "),
        DIFF_COULD_NOT_PRODUCE("Diff could not be produced: "),
        IMPORTANT("Important: "),
        UNAUTHORIZED("Unauthorized: ");

        private final String text;

        Summary(String text) {
            this.text = text;
        }

        String text() {
            return text;
        }
    }

    private Map<Summary, String> summaryMap = new EnumMap<>(Summary.class);

    public MainController(UILauncher uiLauncher) {
        super(uiLauncher);
        statisticService = new StatisticService();
        startupService = new StartupService();
        jobService = new JobService(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        initValues();
        setColumns(diffTableView);
        setColumns(unauthorizedTableView);
        setColumns(importantTableView);
        setProperties();
    }

    private void initValues() {
        selectedValue = new SimpleObjectProperty<>();
        selectedValue.addListener(getSelectedValueChangeListener());
        uiLauncher.bind(selectedValue);
        summaryMap = Arrays.stream(Summary.values()).collect(toMap(summary -> summary, Summary::text));
        diffStringProperty = new SimpleStringProperty(summaryMap.get(Summary.DIFF_COULD_NOT_PRODUCE));
        unauthorizedStringProperty = new SimpleStringProperty(summaryMap.get(Summary.UNAUTHORIZED));
        importantStringProperty = new SimpleStringProperty(summaryMap.get(Summary.IMPORTANT));
        totalStringProperty = new SimpleStringProperty(summaryMap.get(Summary.TOTAL));
        boolean startOnStartupActive = startupService.isStartOnStartupActive();
        autostartCheckBox.setSelected(startOnStartupActive);
        runSchedulerCheckBox.setSelected(jobService.isJobExists());
        cronComboBox.setItems(FXCollections.observableList(Arrays.asList(Crons.values())));
        cronComboBox.setValue(Crons.values()[0]);
        if (failedTries != null && !failedTries.isEmpty()) {
            groupByFilters(failedTries);
        }
    }

    private ChangeListener<ActiveSupportDetails> getSelectedValueChangeListener() {
        return (observable, oldValue, newValue) -> {
            if (newValue == null) {
                List<ActiveSupportDetails> refreshedItems = failedTries.stream()
                        .filter(asd -> !asd.isProcessed())
                        .collect(toList());
                groupByFilters(refreshedItems);
            }
        };
    }

    private void groupByFilters(List<ActiveSupportDetails> total) {
        List<ActiveSupportDetails> diffCouldNotBeProduced = total.stream()
                .filter(getFilterPredicate(Filter.DIFF).or(getFilterPredicate(Filter.REPOSITORIES)))
                .collect(toList());
        List<ActiveSupportDetails> unauthorized = total.stream()
                .filter(getFilterPredicate(Filter.UNAUTHORIZED))
                .collect(toList());
        List<ActiveSupportDetails> important = total.stream()
                .filter(getFilterPredicate(Filter.DIFF).negate())
                .filter(getFilterPredicate(Filter.REPOSITORIES).negate())
                .filter(getFilterPredicate(Filter.UNAUTHORIZED).negate())
                .collect(toList());

        diffTableView.setItems(FXCollections.observableList(diffCouldNotBeProduced));
        unauthorizedTableView.setItems(FXCollections.observableList(unauthorized));
        importantTableView.setItems(FXCollections.observableList(important));

        diffStringProperty.set(summaryMap.get(Summary.DIFF_COULD_NOT_PRODUCE) + diffCouldNotBeProduced.size());
        unauthorizedStringProperty.set(summaryMap.get(Summary.UNAUTHORIZED) + unauthorized.size());
        importantStringProperty.set(summaryMap.get(Summary.IMPORTANT) + important.size());
        totalStringProperty.set(summaryMap.get(Summary.TOTAL) + total.size());
    }

    private Predicate<ActiveSupportDetails> getFilterPredicate(final Filter filter) {
        return asd -> asd.getCause().contains(filter.value());
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

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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
        diffTableView.setRowFactory(getTableViewTableRowCallback());
        importantTableView.setRowFactory(getTableViewTableRowCallback());
        unauthorizedTableView.setRowFactory(getTableViewTableRowCallback());
        diffLabel.textProperty().bindBidirectional(diffStringProperty);
        diffLabel.setOnMouseClicked(getLabelOnClickAction(0));
        unauthorizedLabel.textProperty().bindBidirectional(unauthorizedStringProperty);
        unauthorizedLabel.setOnMouseClicked(getLabelOnClickAction(1));
        importantLabel.textProperty().bindBidirectional(importantStringProperty);
        importantLabel.setOnMouseClicked(getLabelOnClickAction(2));
        totalLabel.textProperty().bindBidirectional(totalStringProperty);
        mainTabPane.setOnMouseClicked(getTabPaneClickAction());
        processButton.setDisable(true);
        processButton.setOnAction(getProcessButtonAction());
        exitButton.setOnAction(getExitButtonAction());
        progressBar.setVisible(false);
        autostartCheckBox.selectedProperty().addListener(getAutostartChangeListener());
        runSchedulerCheckBox.selectedProperty().addListener(getRunSchedulerChangeListener());
    }

    private EventHandler<ActionEvent> getStatisticsEventHandler() {
        return event -> {
            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            progressBar.setVisible(true);
            getStatisticsButton.setDisable(true);

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    LocalDateTime from = LocalDateTime.of(fromDatePicker.getValue(), LocalTime.now());
                    failedTries = statisticService.getFailedTries(from);
                    return null;
                }
            };

            task.setOnSucceeded(ev -> Platform.runLater(() -> {
                groupByFilters(failedTries);
                getStatisticsButton.setDisable(false);
                progressBar.setProgress(1d);
                uiLauncher.displayNotificationMessage(String.format("%d new exceptions downloaded.", failedTries.size()));
            }));

            try {
                ThreadUtils.submit(task);
            } catch (ExecutionException | InterruptedException e) {
                log.error("Something went wrong", e);
            }
        };
    }

    private Callback<TableView<ActiveSupportDetails>, TableRow<ActiveSupportDetails>> getTableViewTableRowCallback() {
        return tv -> {
            TableRow<ActiveSupportDetails> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    selectedValue.setValue(row.getItem());
                    uiLauncher.openDetailsWindow();
                } else if (event.getClickCount() == 1) {
                    processButton.setDisable(false);
                }
            });
            return row;
        };
    }

    private EventHandler<MouseEvent> getLabelOnClickAction(final int tabIndex) {
        return event -> {
            if (event.getClickCount() == 1) {
                mainTabPane.getSelectionModel().select(tabIndex);
                processButton.setDisable(true);
            }
        };
    }

    private EventHandler<MouseEvent> getTabPaneClickAction() {
        return event -> {
            if (event.getClickCount() == 1) {
                processButton.setDisable(true);
            }
        };
    }

    private EventHandler<ActionEvent> getProcessButtonAction() {
        return event -> {
            progressBar.setVisible(true);
            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            processButton.setDisable(true);

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    ObservableList<ActiveSupportDetails> selectedItems;
                    switch (mainTabPane.getSelectionModel().getSelectedIndex()) {
                        case 0:
                            selectedItems = diffTableView.getSelectionModel().getSelectedItems();
                            break;
                        case 1:
                            selectedItems = unauthorizedTableView.getSelectionModel().getSelectedItems();
                            break;
                        default:
                            selectedItems = importantTableView.getSelectionModel().getSelectedItems();
                    }

                    final LocalDateTime processDateTime = LocalDateTime.now();
                    selectedItems.forEach(asd -> {
                        asd.setProcessDateTime(processDateTime);
                        asd.setUserProcessor(System.getProperty("user.name"));
                    });

                    List<ActiveSupport> activeSupports = selectedItems.stream()
                            .map(ActiveSupportDetails::getActiveSupport)
                            .collect(toList());
                    new ActiveSupportService().saveAll(activeSupports);

                    List<ProcessingDetails> processingDetailsList = selectedItems.stream()
                            .map(asd -> new ProcessingDetails(asd.getStatisticId(), asd.getExceptionDetails()))
                            .collect(toList());

                    StatisticService statisticService = new StatisticService();
                    statisticService.processAll(processingDetailsList);

                    LocalDateTime from = LocalDateTime.of(fromDatePicker.getValue(), LocalTime.now());
                    failedTries = statisticService.getFailedTries(from);

                    return null;
                }
            };

            task.setOnSucceeded(evt -> Platform.runLater(() -> {
                groupByFilters(failedTries);
                progressBar.setProgress(1d);
            }));

            try {
                ThreadUtils.submit(task);
            } catch (ExecutionException | InterruptedException e) {
                log.error("Something went wrong", e);
            }

        };
    }

    private EventHandler<ActionEvent> getExitButtonAction() {
        return event -> {
            Platform.exit();
            System.exit(0);
        };
    }

    private ChangeListener<Boolean> getAutostartChangeListener() {
        return (observable, oldValue, newValue) -> {
            if (uiLauncher.isTraySupported()) {
                if (newValue) {
                    if (uiLauncher.isTraySupported()) {
                        log.info("Setting autostart on system startup.");
                        startupService.startOnStartup();
                    }
                } else {
                    log.info("Disabling autostart on system startup.");
                    startupService.disableStartOnStartup();
                }
            }
        };
    }

    public void updateTables(List<ActiveSupportDetails> newData) {
        failedTries = new ArrayList<>(newData);
        Platform.runLater(() -> {
            groupByFilters(failedTries);
            uiLauncher.displayNotificationMessage(String.format("%d new exceptions downloaded.", failedTries.size()));
        });
    }

    private ChangeListener<Boolean> getRunSchedulerChangeListener() {
        return (observable, oldValue, newValue) -> {
            try {
                if (newValue) {
                    jobService.setCrons(cronComboBox.getValue());
                    jobService.scheduleJob();
                } else {
                    jobService.deleteJob();
                }
            } catch (SchedulerException e) {
                log.error("Problem with the job.", e);
            }
        };
    }
}
