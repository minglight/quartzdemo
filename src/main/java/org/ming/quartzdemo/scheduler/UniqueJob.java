package org.ming.quartzdemo.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

@Slf4j
public class UniqueJob implements Job {



    public static final double SUCCESS_RATE = 0.5;

    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {

        final boolean success = RandomUtils.nextFloat(0,1) > SUCCESS_RATE ? true : false;

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.debug("Execution Job : instanceId={},key={}"
            , context.getFireInstanceId()
            , context.getJobDetail().getKey());
        if (success) {
            log.debug("Yes, job success!!");
        } else {
            throw new JobExecutionException("Job failed....");
        }
    }
}
