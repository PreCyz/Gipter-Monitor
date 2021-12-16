package pg.gipter.monitor.ui.details;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.StringConverter;
import pg.gipter.monitor.domain.activeSupports.collections.ActiveSupport;
import pg.gipter.monitor.domain.activeSupports.dto.ProcessingDetails;
import pg.gipter.monitor.domain.activeSupports.services.ActiveSupportService;
import pg.gipter.monitor.domain.statistics.collections.ExceptionDetails;
import pg.gipter.monitor.domain.statistics.services.StatisticService;
import pg.gipter.monitor.ui.AbstractController;
import pg.gipter.monitor.ui.UILauncher;
import pg.gipter.monitor.ui.fxproperties.ActiveSupportDetails;
import pg.gipter.monitor.utils.ThreadUtils;

import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

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
    @FXML
    private Button emailButton;
    @FXML
    private ProgressBar progressBar;

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
        emailButton.setOnAction(sendEmailButtonAction());

        progressBar.setVisible(false);
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
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
            progressBar.setVisible(true);
            if (processedCheckBox.isSelected()) {
                saveButton.setDisable(true);
                saveButton.setDisable(true);
                ActiveSupportDetails activeSupportDetails = selectedValue.getValue();
                activeSupportDetails.setProcessDateTime(LocalDateTime.now());
                activeSupportDetails.setUserProcessor(System.getProperty("user.name"));
                selectedValue.setValue(activeSupportDetails);
                final ActiveSupport activeSupport = selectedValue.getValue().getActiveSupport();
                final ExceptionDetails exceptionDetails = selectedValue.getValue().getExceptionDetails();

                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() {
                        ActiveSupportService activeSupportService = new ActiveSupportService();
                        activeSupportService.save(activeSupport);

                        StatisticService service = new StatisticService();
                        service.setProcessed(new ProcessingDetails(activeSupport.getStatisticId(), exceptionDetails));

                        return null;
                    }
                };

                task.setOnSucceeded(evt -> Platform.runLater(() -> {
                    processedCheckBox.setDisable(true);
                    saveButton.setDisable(true);
                    progressBar.setProgress(1d);
                }));

                try {
                    ThreadUtils.submit(task);
                } catch (ExecutionException | InterruptedException e) {
                    log.error("Something went wrong", e);
                }
            } else {
                log.info("Record not selected to process.");
            }
        };
    }

    private EventHandler<ActionEvent> sendEmailButtonAction() {
        return event -> {
            Desktop desktop;
            if (Desktop.isDesktopSupported()
                    && (desktop = Desktop.getDesktop()).isSupported(Desktop.Action.MAIL)) {

                String email = "mailTo:" + selectedValue.getValue().getUsername() + "@netcompany.com";
                email += "?subject=" + uriEncode("Gipter Active Support");
                email += "&body=" + uriEncode("TBD");

                try {
                    URI mailto = new URI(email);
                    desktop.mail(mailto);
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            } else {
                String email = "mailTo:" + selectedValue.getValue().getUsername() + "@netcompany.com";
                email += "\\?subject=" + uriEncode("Gipter Active Support");
                email += "\\&body=" + uriEncode("TBD");

                String cmd = "";
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    cmd = "cmd.exe /c start " + email;
                } else if (os.contains("osx")) {
                    cmd = "open " + email;
                } else if (os.contains("nix") || os.contains("aix") || os.contains("nux")) {
                    cmd = "xdg-open " + email;
                }
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command(cmd);
                try {
                    processBuilder.start();
                } catch (IOException e) {
                    log.error("Can not send email.", e);
                }
            }
        };
    }

    private static String uriEncode(String in) {
        StringBuilder out = new StringBuilder();
        for (char ch : in.toCharArray()) {
            out.append(Character.isLetterOrDigit(ch) ? ch : String.format("%%%02X", (int) ch));
        }
        return out.toString();
    }

}
