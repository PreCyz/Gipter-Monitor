package pg.gipter.monitor.ui.fxproperties;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.bson.types.ObjectId;
import pg.gipter.monitor.domain.activeSupports.collections.ActiveSupport;
import pg.gipter.monitor.domain.statistics.collections.*;

import java.time.LocalDateTime;
import java.util.*;

public class ActiveSupportDetails {
    private final StringProperty statisticId;
    private final StringProperty username;
    private final ObjectProperty<LocalDateTime> firstExecutionDate;
    private final ObjectProperty<LocalDateTime> lastExecutionDate;
    private final ObjectProperty<LocalDateTime> lastSuccessDate;
    private final ObjectProperty<LocalDateTime> lastFailedDate;
    private final StringProperty javaVersion;
    private final ObjectProperty<UploadStatus> lastUpdateStatus;
    private final ObjectProperty<RunType> lastRunType;
    private final MapProperty<VersionControlSystem, Set<String>> controlSystemMap;
    private final StringProperty applicationVersion;
    private final StringProperty errorMsg;
    private final StringProperty cause;
    private final ObjectProperty<LocalDateTime> errorDate;
    private final BooleanProperty processed;
    private final ObjectProperty<LocalDateTime> processDateTime;
    private final StringProperty userProcessor;
    private final StringProperty processingId;
    private final StringProperty comment;

    public ActiveSupportDetails() {
        this.statisticId = new SimpleStringProperty();
        this.username = new SimpleStringProperty();
        this.firstExecutionDate = new SimpleObjectProperty<>();
        this.lastExecutionDate = new SimpleObjectProperty<>();
        this.lastSuccessDate = new SimpleObjectProperty<>();
        this.lastFailedDate = new SimpleObjectProperty<>();
        this.javaVersion = new SimpleStringProperty();
        this.lastUpdateStatus = new SimpleObjectProperty<>();
        this.lastRunType = new SimpleObjectProperty<>();
        this.controlSystemMap = new SimpleMapProperty<>();
        this.applicationVersion = new SimpleStringProperty();
        this.errorMsg = new SimpleStringProperty();
        this.cause = new SimpleStringProperty();
        this.errorDate = new SimpleObjectProperty<>();
        this.processed = new SimpleBooleanProperty();
        this.processDateTime = new SimpleObjectProperty<>();
        this.userProcessor = new SimpleStringProperty();
        this.comment = new SimpleStringProperty();
        this.processingId = new SimpleStringProperty();
    }

    public static List<ActiveSupportDetails> valueFrom(Statistic statistic) {
        LinkedList<ActiveSupportDetails> result = new LinkedList<>();
        if (statistic != null && statistic.getExceptions() != null && !statistic.getExceptions().isEmpty()) {
            for (ExceptionDetails ed : statistic.getExceptions()) {
                ActiveSupportDetails asd = new ActiveSupportDetails();
                asd.setStatisticId(statistic.getId().toHexString());
                asd.setUsername(statistic.getUsername());
                asd.setFirstExecutionDate(statistic.getFirstExecutionDate());
                asd.setLastExecutionDate(statistic.getLastExecutionDate());
                asd.setLastSuccessDate(statistic.getLastSuccessDate());
                asd.setLastFailedDate(statistic.getLastFailedDate());
                asd.setJavaVersion(statistic.getJavaVersion());
                asd.setLastUpdateStatus(statistic.getLastUpdateStatus());
                asd.setLastRunType(statistic.getLastRunType());
                asd.setControlSystemMap(FXCollections.observableMap(statistic.getControlSystemMap()));
                asd.setApplicationVersion(statistic.getApplicationVersion());
                asd.setErrorMsg(ed.getErrorMsg());
                asd.setCause(ed.getCause());
                asd.setErrorDate(ed.getErrorDate());
                asd.setProcessed(asd.isProcessed());
                asd.setProcessingId(ed.getProcessingId());
                asd.setProcessDateTime(asd.getProcessDateTime());
                asd.setUserProcessor(asd.getUserProcessor());
                asd.setComment(asd.getComment());
                result.add(asd);
            }
        }
        return result;
    }

    public void setStatisticId(String statisticId) {
        this.statisticId.set(statisticId);
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public void setLastSuccessDate(LocalDateTime lastSuccessDate) {
        this.lastSuccessDate.set(lastSuccessDate);
    }

    public void setLastFailedDate(LocalDateTime lastFailedDate) {
        this.lastFailedDate.set(lastFailedDate);
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion.set(javaVersion);
    }

    public void setLastUpdateStatus(UploadStatus lastUpdateStatus) {
        this.lastUpdateStatus.set(lastUpdateStatus);
    }

    public void setLastRunType(RunType lastRunType) {
        this.lastRunType.set(lastRunType);
    }

    public void setControlSystemMap(ObservableMap<VersionControlSystem, Set<String>> controlSystemMap) {
        this.controlSystemMap.set(controlSystemMap);
    }

    public void setApplicationVersion(String applicationVersion) {
        this.applicationVersion.set(applicationVersion);
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg.set(errorMsg);
    }

    public void setCause(String cause) {
        this.cause.set(cause);
    }

    public void setErrorDate(LocalDateTime errorDate) {
        this.errorDate.set(errorDate);
    }

    public void setProcessed(boolean processed) {
        this.processed.set(processed);
    }

    public void setProcessDateTime(LocalDateTime processDateTime) {
        this.processDateTime.set(processDateTime);
    }

    public void setUserProcessor(String userProcessor) {
        this.userProcessor.set(userProcessor);
    }

    public void setComment(String comment) {
        this.comment.set(comment);
    }

    public String getStatisticId() {
        return statisticId.get();
    }

    public StringProperty statisticIdProperty() {
        return statisticId;
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public LocalDateTime getLastSuccessDate() {
        return lastSuccessDate.get();
    }

    public ObjectProperty<LocalDateTime> lastSuccessDateProperty() {
        return lastSuccessDate;
    }

    public LocalDateTime getLastFailedDate() {
        return lastFailedDate.get();
    }

    public ObjectProperty<LocalDateTime> lastFailedDateProperty() {
        return lastFailedDate;
    }

    public String getJavaVersion() {
        return javaVersion.get();
    }

    public StringProperty javaVersionProperty() {
        return javaVersion;
    }

    public UploadStatus getLastUpdateStatus() {
        return lastUpdateStatus.get();
    }

    public ObjectProperty<UploadStatus> lastUpdateStatusProperty() {
        return lastUpdateStatus;
    }

    public RunType getLastRunType() {
        return lastRunType.get();
    }

    public ObjectProperty<RunType> lastRunTypeProperty() {
        return lastRunType;
    }

    public ObservableMap<VersionControlSystem, Set<String>> getControlSystemMap() {
        return controlSystemMap.get();
    }

    public MapProperty<VersionControlSystem, Set<String>> controlSystemMapProperty() {
        return controlSystemMap;
    }

    public String getApplicationVersion() {
        return applicationVersion.get();
    }

    public StringProperty applicationVersionProperty() {
        return applicationVersion;
    }

    public String getErrorMsg() {
        return errorMsg.get();
    }

    public StringProperty errorMsgProperty() {
        return errorMsg;
    }

    public String getCause() {
        return cause.get();
    }

    public StringProperty causeProperty() {
        return cause;
    }

    public LocalDateTime getErrorDate() {
        return errorDate.get();
    }

    public ObjectProperty<LocalDateTime> errorDateProperty() {
        return errorDate;
    }

    public boolean isProcessed() {
        return processed.get();
    }

    public BooleanProperty processedProperty() {
        return processed;
    }

    public LocalDateTime getProcessDateTime() {
        return processDateTime.get();
    }

    public ObjectProperty<LocalDateTime> processDateTimeProperty() {
        return processDateTime;
    }

    public String getUserProcessor() {
        return userProcessor.get();
    }

    public StringProperty userProcessorProperty() {
        return userProcessor;
    }

    public String getComment() {
        return comment.get();
    }

    public StringProperty commentProperty() {
        return comment;
    }

    public LocalDateTime getFirstExecutionDate() {
        return firstExecutionDate.get();
    }

    public ObjectProperty<LocalDateTime> firstExecutionDateProperty() {
        return firstExecutionDate;
    }

    public void setFirstExecutionDate(LocalDateTime firstExecutionDate) {
        this.firstExecutionDate.set(firstExecutionDate);
    }

    public LocalDateTime getLastExecutionDate() {
        return lastExecutionDate.get();
    }

    public ObjectProperty<LocalDateTime> lastExecutionDateProperty() {
        return lastExecutionDate;
    }

    public void setLastExecutionDate(LocalDateTime lastExecutionDate) {
        this.lastExecutionDate.set(lastExecutionDate);
    }

    public String getProcessingId() {
        return processingId.get();
    }

    public StringProperty processingIdProperty() {
        return processingId;
    }

    public void setProcessingId(String processingId) {
        this.processingId.set(processingId);
    }

    public ExceptionDetails getExceptionDetails() {
        return new ExceptionDetails(getErrorMsg(), getCause(), getErrorDate(), getProcessingId());
    }

    public ActiveSupport getActiveSupport() {
        ObjectId activeSupportId = new ObjectId();
        setProcessingId(activeSupportId.toHexString());
        return new ActiveSupport(
                activeSupportId, getStatisticId(), isProcessed(), getProcessDateTime(), getUserProcessor(), getComment()
        );
    }
}
