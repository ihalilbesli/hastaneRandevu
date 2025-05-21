package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.AccessLog;
import com.hastanerandevu.app.repository.AccessLogRepository;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.AccessLogService;
import com.hastanerandevu.app.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccessLogServiceImpl implements AccessLogService {
    private final AccessLogRepository accessLogRepository;
    private final UserRepository userRepository;

    public AccessLogServiceImpl(AccessLogRepository accessLogRepository, UserRepository userRepository) {
        this.accessLogRepository = accessLogRepository;
        this.userRepository = userRepository;
    }
    private void checkAdminAccess() {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Bu işlemi sadece admin gerçekleştirebilir.");
        }
    }
    @Override
    public List<AccessLog> getAllLogs() {
        checkAdminAccess();
        return accessLogRepository.findAll();
    }

    @Override
    public List<AccessLog> getLogsByEmail(String email) {
        checkAdminAccess();
        return accessLogRepository.findByUserEmail(email);
    }

    @Override
    public List<AccessLog> getLogsByRole(String role) {
        checkAdminAccess();
        return accessLogRepository.findByRole(role.toUpperCase());
    }

    @Override
    public List<AccessLog> getLogsByStatus(String status) {
        checkAdminAccess();
        return accessLogRepository.findByStatus(status.toUpperCase());
    }
    @Override
    public void saveLog(AccessLog log) {
        accessLogRepository.save(log);
    }

    @Override
    public List<AccessLog> getLogsByPeriod(String period) {
        checkAdminAccess();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = switch (period.toLowerCase()) {
            case "day" -> now.minusDays(1);
            case "week" -> now.minusWeeks(1);
            case "month" -> now.minusMonths(1);
            case "year" -> now.minusYears(1);
            default -> throw new IllegalArgumentException("Geçersiz zaman aralığı: " + period);
        };

        return accessLogRepository.findByTimestampBetween(start, now);
    }
}
