package pg.gipter.monitor.domain.activeSupports.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pg.gipter.monitor.domain.statistics.collections.ExceptionDetails;

@AllArgsConstructor
@Getter
public class ProcessingDetails {
    private String statisticId;
    private ExceptionDetails exceptionDetails;
}
