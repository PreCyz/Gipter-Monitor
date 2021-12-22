package pg.gipter.monitor.domain.statistics.services;

import lombok.extern.slf4j.Slf4j;
import pg.gipter.monitor.domain.activeSupports.dto.ProcessingDetails;
import pg.gipter.monitor.domain.statistics.dao.StatisticDao;
import pg.gipter.monitor.domain.statistics.dao.StatisticDaoFactory;
import pg.gipter.monitor.ui.fxproperties.ActiveSupportDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/** Created by Pawel Gawedzki on 06-Dec-2021. */
@Slf4j
public class StatisticService {

    private final StatisticDao statisticDao;

    private static class InstanceHolder {
        static final StatisticService INSTANCE_HOLDER = new StatisticService();
    }

    private StatisticService() {
        statisticDao = StatisticDaoFactory.getStatisticDao();
    }

    public static StatisticService getInstance() {
        return InstanceHolder.INSTANCE_HOLDER;
    }

    public List<ActiveSupportDetails> getFailedTries(final LocalDateTime localDateTime) {
        log.info("Getting statistics");
        return statisticDao.findAllByLastFailedDateAfter(localDateTime).stream()
                .map(ActiveSupportDetails::valueFrom)
                .flatMap(List::stream)
                .filter(asd -> asd.getErrorDate().isAfter(localDateTime))
                .filter(asd -> asd.getProcessingId() == null || asd.getProcessingId().isEmpty())
                .collect(toList());
    }

    public void saveProcessed(ProcessingDetails processingDetails) {
        log.info("Processing statistic with id [{}].", processingDetails.getStatisticId());
        log.info("Processing statistic with id [{}].", processingDetails.getStatisticId());
        statisticDao.saveProcessed(processingDetails);
    }

    public void processAll(List<ProcessingDetails> processingDetailsList) {
        log.info("Processing statistics with ids [{}].", processingDetailsList.stream()
                .map(ProcessingDetails::getStatisticId)
                .collect(Collectors.joining(","))
        );
        statisticDao.saveAllProcessed(processingDetailsList);
    }
}
