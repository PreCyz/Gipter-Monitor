package pg.gipter.monitor.domain.statistics.dao;


import pg.gipter.monitor.domain.activeSupports.dto.ProcessingDetails;
import pg.gipter.monitor.domain.statistics.collections.Statistic;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatisticDao {

    Collection<Statistic> findAllByLastFailedDateAfter(LocalDateTime localDateTime);
    boolean isStatisticsAvailable();
    void saveProcessed(ProcessingDetails processingDetails);
    void saveAllProcessed(List<ProcessingDetails> processingDetailsList);
}
