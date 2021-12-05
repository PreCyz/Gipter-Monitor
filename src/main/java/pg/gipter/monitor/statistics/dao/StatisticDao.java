package pg.gipter.monitor.statistics.dao;


import pg.gipter.monitor.statistics.Statistic;

public interface StatisticDao {

    void updateStatistics(Statistic user);
    boolean isStatisticsAvailable();
}
