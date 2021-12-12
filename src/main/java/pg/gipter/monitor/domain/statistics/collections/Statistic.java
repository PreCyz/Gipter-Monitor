package pg.gipter.monitor.domain.statistics.collections;

import lombok.*;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class Statistic implements Serializable {

    public static final String COLLECTION_NAME = "statistics";

    private ObjectId id;
    private String username;
    private LocalDateTime lastExecutionDate;
    private LocalDateTime firstExecutionDate;
    private LocalDateTime lastSuccessDate;
    private LocalDateTime lastFailedDate;
    private String javaVersion;
    private UploadStatus lastUpdateStatus;
    private RunType lastRunType;
    private Map<VersionControlSystem, Set<String>> controlSystemMap;
    private Set<String> systemUsers;
    private String applicationVersion;
    private List<ExceptionDetails> exceptions;
}
