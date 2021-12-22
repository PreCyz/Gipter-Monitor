package pg.gipter.monitor.jobs;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import pg.gipter.monitor.domain.statistics.services.StatisticService;
import pg.gipter.monitor.ui.fxproperties.ActiveSupportDetails;
import pg.gipter.monitor.ui.main.MainController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class GetStatisticsJob implements Job {

    public static final String NAME = GetStatisticsJob.class.getSimpleName();
    public static final String GROUP = NAME + "Group";

    @Override
    public void execute(JobExecutionContext context) {
        log.info("Executing job {}.", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        getStatistics(context.getMergedJobDataMap());
    }

    private void getStatistics(JobDataMap jobDataMap) {
        MainController mainController = (MainController) jobDataMap.get(MainController.class.getSimpleName());
        LocalDateTime fromDate = LocalDateTime.now().minusDays(5);

        List<ActiveSupportDetails> failedTries = StatisticService.getInstance().getFailedTries(fromDate);
        mainController.updateTables(failedTries);
    }
}
