package pg.gipter.monitor.ui.details;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import pg.gipter.monitor.ui.AbstractController;
import pg.gipter.monitor.ui.UILauncher;
import pg.gipter.monitor.ui.fxproperties.ActiveSupportDetails;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class DetailsController extends AbstractController {

    @FXML
    private Label statisticIdLabel;
    @FXML
    private Label usernameLabel;
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
        initValues();
        setProperties();
    }

    private void initValues() {
        selectedValue = uiLauncher.getCurrentActiveSupport();
        statisticIdLabel.setText(selectedValue.get().getStatisticId());
        usernameLabel.setText(selectedValue.get().getUsername());
        lastSuccessDateLabel.setText(Optional.ofNullable(selectedValue.get().getLastSuccessDate())
                .map(ldt -> ldt.format(YYYY_MM_DD_HH_MM))
                .orElseGet(() -> "")
        );
        lastFailedDateLabel.setText(Optional.ofNullable(selectedValue.get().getLastFailedDate())
                .map(ldt -> ldt.format(YYYY_MM_DD_HH_MM))
                .orElseGet(() -> "")
        );
        javaVersionLabel.setText(selectedValue.get().getJavaVersion());
        lastUpdateStatusLabel.setText(selectedValue.get().getLastUpdateStatus().name());
        lastRunTypeLabel.setText(selectedValue.get().getLastRunType().name());
        controlSystemMapLabel.setText(selectedValue.get().getControlSystemMap().toString());
        applicationVersionLabel.setText(selectedValue.get().getApplicationVersion());
        errorMsgLabel.setText(selectedValue.get().getErrorMsg());
        causeLabel.setText(selectedValue.get().getCause());
        errorDateLabel.setText(Optional.ofNullable(selectedValue.get().getErrorDate())
                .map(ldt -> ldt.format(YYYY_MM_DD_HH_MM))
                .orElseGet(() -> "")
        );
        processedCheckBox.setSelected(selectedValue.get().isProcessed());
        processedCheckBox.selectedProperty().bindBidirectional(selectedValue.get().processedProperty());

        processDateTimeLabel.setText(Optional.ofNullable(selectedValue.get().getProcessDateTime())
                .map(ldt -> ldt.format(YYYY_MM_DD_HH_MM))
                .orElseGet(() -> "")
        );
        userProcessorLabel.setText(selectedValue.get().getUserProcessor());
        commentTextArea.setText(selectedValue.get().getComment());
        commentTextArea.textProperty().bindBidirectional(selectedValue.get().commentProperty());
    }

    private void setProperties() {
        statisticIdLabel.setTooltip(createTooltip("Statistic ID"));
        usernameLabel.setTooltip(createTooltip("Username"));
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

        cancelButton.setOnAction(event -> uiLauncher.closeDetailsWindow());
        saveButton.setOnAction(getSaveButtonAction());
    }

    private EventHandler<ActionEvent> getSaveButtonAction() {
        return event -> {
            System.out.printf("Is processed %b%n", selectedValue.getValue().isProcessed());
        };
    }

    private Tooltip createTooltip(String text) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setTextAlignment(TextAlignment.LEFT);
        tooltip.setFont(Font.font("Courier New", 14));
        return tooltip;
    }

}
