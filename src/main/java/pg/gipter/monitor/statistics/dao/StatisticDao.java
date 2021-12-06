package pg.gipter.monitor.statistics.dao;


import pg.gipter.monitor.statistics.Statistic;

import java.time.LocalDateTime;
import java.util.Collection;

public interface StatisticDao {

    Collection<Statistic> findAllByLastFailedDateAfter(LocalDateTime localDateTime);
    boolean isStatisticsAvailable();
}
