package com.hastanerandevu.app.controller;

import com.hastanerandevu.app.model.AccessLog;
import com.hastanerandevu.app.service.AccessLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hastarandevu/acces-log")
public class AccessLogController {
    private AccessLogService accessLogService;

    public AccessLogController(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
    }
    // ✅ Tüm erişim kayıtlarını getir
    @GetMapping
    public ResponseEntity<List<AccessLog>> getAllLogs() {
        return ResponseEntity.ok(accessLogService.getAllLogs());
    }

    // ✅ Kullanıcı email ile logları getir
    @GetMapping("/email/{email}")
    public ResponseEntity<List<AccessLog>> getLogsByEmail(@PathVariable String email) {
        return ResponseEntity.ok(accessLogService.getLogsByEmail(email));
    }

    // ✅ Role göre logları getir
    @GetMapping("/role/{role}")
    public ResponseEntity<List<AccessLog>> getLogsByRole(@PathVariable String role) {
        return ResponseEntity.ok(accessLogService.getLogsByRole(role));
    }

    // ✅ Duruma göre logları getir (SUCCESS, ERROR)
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AccessLog>> getLogsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(accessLogService.getLogsByStatus(status));
    }

    // ✅ Zaman filtresine göre logları getir (day, week, month, year)
    @GetMapping("/filter")
    public ResponseEntity<List<AccessLog>> getLogsByPeriod(@RequestParam String period) {
        return ResponseEntity.ok(accessLogService.getLogsByPeriod(period));
    }
}
