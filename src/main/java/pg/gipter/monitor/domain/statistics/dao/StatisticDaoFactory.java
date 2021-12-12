package pg.gipter.monitor.domain.statistics.dao;

public final class StatisticDaoFactory {
    private StatisticDaoFactory() { }

    public static StatisticDao getStatisticDao() {
        return new StatisticRepository();
    }
}
