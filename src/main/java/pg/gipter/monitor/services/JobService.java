package pg.gipter.monitor.services;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import pg.gipter.monitor.jobs.GetStatisticsJob;
import pg.gipter.monitor.ui.main.MainController;

import java.text.ParseException;

import static org.quartz.TriggerBuilder.newTrigger;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JobService {

    final static TriggerKey TRIGGER_KEY = new TriggerKey("get-exceptions", "get-exceptions");
    private final JobKey jobKey = new JobKey(GetStatisticsJob.NAME, GetStatisticsJob.GROUP);
    private Trigger trigger;

    private Scheduler scheduler;
    private MainController mainController;
    @Setter
    private Crons crons;

    private static class InstanceHolder {
        static final JobService INSTANCE_HOLDER = new JobService();
    }

    public static JobService getInstance(final MainController mainController) {
        if (InstanceHolder.INSTANCE_HOLDER.mainController == null) {
            InstanceHolder.INSTANCE_HOLDER.mainController = mainController;
        }
        return InstanceHolder.INSTANCE_HOLDER;
    }

    public void scheduleJob() {
        try {
            if (!isSchedulerInitiated()) {
                scheduler = StdSchedulerFactory.getDefaultScheduler();
                log.info("Default scheduler created.");
            }
            try {

                if (scheduler.checkExists(getJobKey())) {
                    log.info("Job with key [{}] already exists. No need to schedule it again.", getJobKey());
                } else {
                    createTrigger();
                    scheduler.scheduleJob(create(), getTrigger());
                    log.info("New job scheduled with the following frequency [{}].", crons.name());
                }

            } catch (ParseException | SchedulerException ex) {
                log.error("Can not schedule job.", ex);
            }
            scheduler.start();
        } catch (SchedulerException ex) {
            log.error("Can not schedule a new job.", ex);
        }
    }

    private JobDetail create() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(MainController.class.getSimpleName(), mainController);

        return JobBuilder.newJob(GetStatisticsJob.class)
                .withIdentity(GetStatisticsJob.NAME, GetStatisticsJob.GROUP)
                .usingJobData(jobDataMap)
                .build();
    }

    private void createTrigger() throws ParseException {
        CronExpression expression = new CronExpression(crons.expression());
        trigger = newTrigger()
                .withIdentity(TRIGGER_KEY.getName(), TRIGGER_KEY.getGroup())
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule(expression))
                .build();
    }

    public void deleteJob() {
        try {
            scheduler.deleteJob(jobKey);
            log.info("{} job deleted.", jobKey.getName());
        } catch (SchedulerException ex) {
            log.error("Can not delete job.", ex);
        }
    }

    private Trigger getTrigger() {
        return trigger;
    }

    private JobKey getJobKey() {
        return jobKey;
    }

    private boolean isSchedulerInitiated() {
        return scheduler != null;
    }

    public boolean isJobExists() {
        try {
            return isSchedulerInitiated() && scheduler.checkExists(jobKey);
        } catch (SchedulerException e) {
            log.error("Can not determine if job was defined.", e);
            return false;
        }
    }

}
