package pg.gipter.monitor.statistics.services;

import lombok.extern.slf4j.Slf4j;
import pg.gipter.monitor.statistics.Statistic;
import pg.gipter.monitor.statistics.dao.StatisticDao;
import pg.gipter.monitor.statistics.dao.StatisticDaoFactory;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/** Created by Pawel Gawedzki on 06-Dec-2021. */
@Slf4j
public class StatisticService {

    private final StatisticDao statisticDao;

    public StatisticService() {
        statisticDao = StatisticDaoFactory.getStatisticDao();
    }

    public List<Statistic> getFailedTries(LocalDateTime localDateTime) {
        log.info("Getting statistics");
        return new LinkedList<>(statisticDao.findAllByLastFailedDateAfter(localDateTime));
    }

}
