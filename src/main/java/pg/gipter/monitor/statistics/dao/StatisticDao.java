package pg.gipter.monitor.statistics.dao;


import pg.gipter.monitor.statistics.collections.Statistic;
import pg.gipter.monitor.ui.fxproperties.ActiveSupportDetails;

import java.time.LocalDateTime;
import java.util.Collection;

public interface StatisticDao {

    Collection<Statistic> findAllByLastFailedDateAfter(LocalDateTime localDateTime);
    boolean isStatisticsAvailable();
    void saveProcessed(ActiveSupportDetails selectedValue);
}
