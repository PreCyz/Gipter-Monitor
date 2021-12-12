package pg.gipter.monitor.ui.details;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.StringConverter;
import pg.gipter.monitor.domain.activeSupports.collections.ActiveSupport;
import pg.gipter.monitor.domain.activeSupports.services.ActiveSupportService;
import pg.gipter.monitor.domain.statistics.services.StatisticService;
import pg.gipter.monitor.ui.AbstractController;
import pg.gipter.monitor.ui.UILauncher;
import pg.gipter.monitor.ui.fxproperties.ActiveSupportDetails;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class DetailsController extends AbstractController {

    @FXML
    private Label statisticIdLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label firstExecutionDateLabel;
    @FXML
    private Label lastExecutionDateLabel;
    @FXML
    private Label lastSuccessDateLabel;
    @FXML
    private Label lastFailedDateLabel;
    @FXML
    private Label javaVersionLabel;
    @FXML
    private Label lastUpdateStatusLabel;
    @FXML
    private Label lastRunTypeLabel;
    @FXML
    private Label controlSystemMapLabel;
    @FXML
    private Label applicationVersionLabel;
    @FXML
    private Label errorMsgLabel;
    @FXML
    private Label causeLabel;
    @FXML
    private Label errorDateLabel;
    @FXML
    private CheckBox processedCheckBox;
    @FXML
    private Label processDateTimeLabel;
    @FXML
    private Label userProcessorLabel;
    @FXML
    private TextArea commentTextArea;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private SimpleObjectProperty<ActiveSupportDetails> selectedValue;
    private final DateTimeFormatter YYYY_MM_DD_HH_MM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public DetailsController(UILauncher uiLauncher) {
        super(uiLauncher);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        selectedValue = uiLauncher.getCurrentActiveSupport();
        bindProperties();
        initValues();
        setProperties();
    }

    private void bindProperties() {
        processedCheckBox.selectedProperty().bindBidirectional(selectedValue.get().processedProperty());
        processDateTimeLabel.textProperty()
                .bindBidirectional(selectedValue.get().processDateTimeProperty(), getLocalDateTimeConverter());
        userProcessorLabel.textProperty().bindBidirectional(selectedValue.get().userProcessorProperty());
        commentTextArea.textProperty().bindBidirectional(selectedValue.get().commentProperty());
    }

    private StringConverter<LocalDateTime> getLocalDateTimeConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(LocalDateTime object) {
                if (object == null) {
                    return "";
                }
                return YYYY_MM_DD_HH_MM.format(object);
            }

            @Override
            public LocalDateTime fromString(String string) {
                if (string.isEmpty()) {
                    return null;
                }
                return LocalDateTime.from(YYYY_MM_DD_HH_MM.parse(string));
            }
        };
    }

    private void initValues() {
        statisticIdLabel.setText(selectedValue.getValue().getStatisticId());
        usernameLabel.setText(selectedValue.getValue().getUsername());
        firstExecutionDateLabel.setText(Optional.ofNullable(selectedValue.getValue().getFirstExecutionDate())
                .map(ldt -> ldt.format(YYYY_MM_DD_HH_MM))
                .orElseGet(() -> "")
        );
        lastExecutionDateLabel.setText(Optional.ofNullable(selectedValue.getValue().getLastExecutionDate())
                .map(ldt -> ldt.format(YYYY_MM_DD_HH_MM))
                .orElseGet(() -> "")
        );
        lastSuccessDateLabel.setText(Optional.ofNullable(selectedValue.getValue().getLastSuccessDate())
                .map(ldt -> ldt.format(YYYY_MM_DD_HH_MM))
                .orElseGet(() -> "")
        );
        lastFailedDateLabel.setText(Optional.ofNullable(selectedValue.getValue().getLastFailedDate())
                .map(ldt -> ldt.format(YYYY_MM_DD_HH_MM))
                .orElseGet(() -> "")
        );
        javaVersionLabel.setText(selectedValue.getValue().getJavaVersion());
        lastUpdateStatusLabel.setText(selectedValue.getValue().getLastUpdateStatus().name());
        lastRunTypeLabel.setText(selectedValue.getValue().getLastRunType().name());
        controlSystemMapLabel.setText(selectedValue.getValue().getControlSystemMap().toString());
        applicationVersionLabel.setText(selectedValue.getValue().getApplicationVersion());

        errorMsgLabel.setText(selectedValue.getValue().getErrorMsg());
        causeLabel.setText(selectedValue.getValue().getCause());
        errorDateLabel.setText(Optional.ofNullable(selectedValue.getValue().getErrorDate())
                .map(ldt -> ldt.format(YYYY_MM_DD_HH_MM))
                .orElseGet(() -> "")
        );
    }

    private void setProperties() {
        statisticIdLabel.setTooltip(createTooltip("Statistic ID"));
        usernameLabel.setTooltip(createTooltip("Username"));
        firstExecutionDateLabel.setTooltip(createTooltip("First execution date"));
        lastExecutionDateLabel.setTooltip(createTooltip("Last execution date"));
        lastSuccessDateLabel.setTooltip(createTooltip("Last success date"));
        lastFailedDateLabel.setTooltip(createTooltip("Last failed date"));
        javaVersionLabel.setTooltip(createTooltip("Java version"));
        lastUpdateStatusLabel.setTooltip(createTooltip("Last update status"));
        lastRunTypeLabel.setTooltip(createTooltip("Last rune type"));
        controlSystemMapLabel.setTooltip(createTooltip("VCS"));
        applicationVersionLabel.setTooltip(createTooltip("Application version"));
        errorMsgLabel.setTooltip(createTooltip("Error message"));
        causeLabel.setTooltip(createTooltip("Cause"));
        errorDateLabel.setTooltip(createTooltip("Error date"));
        processDateTimeLabel.setTooltip(createTooltip("Process date"));
        userProcessorLabel.setTooltip(createTooltip("User processor"));
        commentTextArea.setTooltip(createTooltip("Comment"));

        processedCheckBox.setDisable(selectedValue.get().isProcessed());

        cancelButton.setOnAction(getCancelButtonAction());
        saveButton.setDisable(selectedValue.getValue().isProcessed());
        saveButton.setOnAction(getSaveButtonAction());
    }

    private Tooltip createTooltip(String text) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setTextAlignment(TextAlignment.LEFT);
        tooltip.setFont(Font.font("Courier New", 14));
        return tooltip;
    }

    private EventHandler<ActionEvent> getCancelButtonAction() {
        return event -> {
            if (selectedValue.getValue().isProcessed() && processedCheckBox.isDisabled()) {
                selectedValue.set(null);
            }
            uiLauncher.closeDetailsWindow();
        };
    }

    private EventHandler<ActionEvent> getSaveButtonAction() {
        return event -> {
            if (processedCheckBox.isSelected()) {
                ActiveSupportDetails activeSupportDetails = selectedValue.getValue();
                activeSupportDetails.setProcessDateTime(LocalDateTime.now());
                activeSupportDetails.setUserProcessor(System.getProperty("user.name"));
                selectedValue.setValue(activeSupportDetails);

                ActiveSupportService activeSupportService = new ActiveSupportService();
                ActiveSupport savedActiveSupport = activeSupportService.save(selectedValue.getValue().getActiveSupport());
                selectedValue.getValue().setProcessingId(savedActiveSupport.getId().toHexString());

                StatisticService service = new StatisticService();
                service.setProcessed(
                        selectedValue.getValue().getStatisticId(),
                        selectedValue.getValue().getExceptionDetails(),
                        savedActiveSupport.getId().toHexString()
                );
                processedCheckBox.setDisable(true);
                saveButton.setDisable(true);
            } else {
                log.info("Record not selected to process.");
            }
        };
    }

}
