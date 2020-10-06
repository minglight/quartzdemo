package org.ming.quartzdemo.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@Slf4j
@Service
public class ScheduleManager {

    public static ScheduleManager instance;


    public static final String DEFAULT_TRIGGER_GROUP = "state-machine-trigger-group";
    public final String DEFAULT_JOB_GROUP = "state-machine-job-group";

    /***
     * The default scheduler which use the default setting in the application.yml
     */
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private RetryJobListener retryJobListener;

    @PostConstruct
    void init() throws SchedulerException {
        instance = this;
        scheduler.getListenerManager().addJobListener(retryJobListener);
    }


    public void executeJob(final String jobId) throws SchedulerException {
        final JobDetail job = JobBuilder.newJob(UniqueJob.class)
            .withIdentity(jobId, this.DEFAULT_JOB_GROUP)
            //when job is not finished but the scheduler terminated, request the job and trigger to resume again
            .requestRecovery()
            .build();

        // Trigger the job to run now
        final Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(jobId, DEFAULT_TRIGGER_GROUP)
            .startNow()
            .build();

        // Tell quartz to schedule the job using our trigger
        this.scheduler.scheduleJob(job, trigger);
        log.debug("Schedule Job : jobId={}", jobId);
    }

    /***
     *
     * @param jobId the jobId that want to reschedule
     * @param retry the retry times of the job, it will reschedule the trigger after ( 5 seconds * retry_times )
     * @throws SchedulerException
     */
    public void rescheduleJob(final String jobId, final Integer retry) throws SchedulerException {

        final int delay = (retry + 1) * 5000;


        final Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(jobId, DEFAULT_TRIGGER_GROUP)
            .forJob(jobId)
            .usingJobData("retry", retry + 1)
            .startAt(new Date(System.currentTimeMillis() + delay))
            .withSchedule(simpleSchedule())
            .build();

        this.scheduler.rescheduleJob(TriggerKey.triggerKey(jobId, DEFAULT_TRIGGER_GROUP), trigger);

        log.debug("Reschedule Job : jobId={}, delay={}, retry={}", jobId, delay, (retry + 1));

    }

}
