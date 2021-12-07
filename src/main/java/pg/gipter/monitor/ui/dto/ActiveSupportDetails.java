package pg.gipter.monitor.ui.dto;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import pg.gipter.monitor.statistics.collections.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class ActiveSupportDetails {
    private ObjectId id;
    private String username;
    private LocalDateTime lastSuccessDate;
    private LocalDateTime lastFailedDate;
    private String javaVersion;
    private UploadStatus lastUpdateStatus;
    private RunType lastRunType;
    private Map<VersionControlSystem, Set<String>> controlSystemMap;
    private String applicationVersion;
    private ExceptionDetails exception;
    private ActiveSupport activeSupport;
}
