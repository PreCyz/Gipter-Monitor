package pg.gipter.monitor.domain.statistics.services;

import lombok.extern.slf4j.Slf4j;
import pg.gipter.monitor.domain.statistics.collections.ExceptionDetails;
import pg.gipter.monitor.domain.statistics.dao.StatisticDao;
import pg.gipter.monitor.domain.statistics.dao.StatisticDaoFactory;
import pg.gipter.monitor.ui.fxproperties.ActiveSupportDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/** Created by Pawel Gawedzki on 06-Dec-2021. */
@Slf4j
public class StatisticService {

    private final StatisticDao statisticDao;

    public StatisticService() {
        statisticDao = StatisticDaoFactory.getStatisticDao();
    }

    public List<ActiveSupportDetails> getFailedTries(final LocalDateTime localDateTime) {
        log.info("Getting statistics");
        return statisticDao.findAllByLastFailedDateAfter(localDateTime).stream()
                .map(ActiveSupportDetails::valueFrom)
                .flatMap(List::stream)
                .filter(asd -> asd.getErrorDate().isAfter(localDateTime))
                .collect(Collectors.toList());
    }

    public void setProcessed(String statisticId, ExceptionDetails exceptionDetails, String processId) {
        log.info("Processing statistic with id [{}].", statisticId);

        statisticDao.saveProcessed(statisticId, exceptionDetails, processId);
    }
}
