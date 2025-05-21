package com.hastanerandevu.app.service;

import com.hastanerandevu.app.model.AccessLog;

import java.util.List;

public interface AccessLogService {

    List<AccessLog> getAllLogs();

    List<AccessLog> getLogsByEmail(String email);

    List<AccessLog> getLogsByRole(String role);

    List<AccessLog> getLogsByStatus(String status);

    List<AccessLog> getLogsByPeriod(String period);

    void saveLog(AccessLog log);
}
