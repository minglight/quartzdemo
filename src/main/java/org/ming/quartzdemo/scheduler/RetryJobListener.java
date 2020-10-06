package org.ming.quartzdemo.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.SchedulerException;
import org.quartz.listeners.JobListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RetryJobListener extends JobListenerSupport {

    public static final int MAX_RETRY = 2;

    @Autowired
    private ScheduleManager scheduleManager;

    @Override
    public String getName() {
        return "RetryJobListener";
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        if(jobException != null){
            retryJob(context);
        }
    }

    private void retryJob(JobExecutionContext context) {
        final Integer retry = context.getMergedJobDataMap().get("retry") == null ? 0 :
            Integer.valueOf(context.getMergedJobDataMap().get("retry").toString());
        if (retry <= MAX_RETRY) {
            log.debug("job failed, call reschedule: jobId={}", context.getJobDetail().getKey().getName());
            try {
                    scheduleManager.rescheduleJob(context.getJobDetail().getKey().getName(), retry);
            } catch (SchedulerException e) {
                log.error("Retry failed!!", e);
            }
        } else {
            log.debug("failed finally, freeze this job");
        }
    }
}
