package pg.gipter.monitor.domain.activeSupports.collections;

import lombok.*;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActiveSupport {

    public static final String COLLECTION_NAME = "activeSupports";

    private ObjectId id;
    private Boolean processed;
    private LocalDateTime processDateTime;
    private String userProcessor;
    private String comment;
}
