package com.clouway.bankapp.adapter.web.filter.util;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.clouway.bankapp.core.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class SessionCleanupTask extends AbstractScheduledService {

    private SessionRepository sessionRepo;

    public SessionCleanupTask(SessionRepository sessionRepo){
        this.sessionRepo = sessionRepo;
    }

    @Override
    public void startUp() throws SQLException {

    }


    /**
     * Cleans up all sessions whose expiration dates
     * have been reached.
     */
    @Override
    protected void runOneIteration() {

        sessionRepo.deleteSessionsExpiringAfter(Timestamp.valueOf(LocalDateTime.now()));
    }

    /**
     * Schedules a clean up task to be run every hour.
     *
     * @return
     */
    @Override
    protected Scheduler scheduler() {
        return Scheduler
                .newFixedRateSchedule(1, 1, TimeUnit.HOURS);
    }
}
