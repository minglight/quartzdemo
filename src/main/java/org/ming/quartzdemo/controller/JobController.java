package org.ming.quartzdemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.ming.quartzdemo.scheduler.SimpleJobManager;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class JobController {


    @Autowired
    private SimpleJobManager simpleJobManager;

    @PostMapping("/job")

    public String startJob(@RequestParam final String id) throws SchedulerException {

        // define the job and tie it to our HelloJob class
        try {
            this.simpleJobManager.executeJob(id, 2);
            return "OK";
        } catch (final ObjectAlreadyExistsException ex) {
            log.info("Job is under execution, please wait, id={}", id);
            return "WAIT";
        }

    }
}
