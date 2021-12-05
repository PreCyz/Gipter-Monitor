package pg.gipter.monitor.statistics.dao;

public final class StatisticDaoFactory {
    private StatisticDaoFactory() { }

    public static StatisticDao getStatisticDao() {
        return new StatisticRepository();
    }
}
