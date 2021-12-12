package pg.gipter.monitor.domain.statistics.dao;


import pg.gipter.monitor.domain.statistics.collections.ExceptionDetails;
import pg.gipter.monitor.domain.statistics.collections.Statistic;

import java.time.LocalDateTime;
import java.util.Collection;

public interface StatisticDao {

    Collection<Statistic> findAllByLastFailedDateAfter(LocalDateTime localDateTime);
    boolean isStatisticsAvailable();
    void saveProcessed(String statisticId, ExceptionDetails exceptionDetails, String processId);
}
