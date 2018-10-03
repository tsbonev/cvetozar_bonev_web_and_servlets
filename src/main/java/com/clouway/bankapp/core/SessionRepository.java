package com.clouway.bankapp.core;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface SessionRepository {

    void registerSession(Session session);
    void refreshSession(Session session);
    void terminateSession(String sessionId);
    void deleteSessionsExpiringAfter(Timestamp timestamp);
    Optional<Session> getSessionAvailableAt(String sessionId, Timestamp timestamp);
    int getActiveSessionsCount();

}
