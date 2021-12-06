package pg.gipter.monitor.statistics;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExceptionDetails implements Serializable {

    private String errorMsg;
    private String cause;
    private LocalDateTime errorDate;

}
