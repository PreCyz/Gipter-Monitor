package pg.gipter.monitor.statistics.collections;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActiveSupport {
    private Boolean processed;
    private LocalDateTime processDateTime;
    private String userProcessor;
    private String comment;
}
