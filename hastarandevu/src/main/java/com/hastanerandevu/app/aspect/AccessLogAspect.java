package com.hastanerandevu.app.aspect;

import com.hastanerandevu.app.model.AccessLog;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.AccessLogService;
import com.hastanerandevu.app.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
public class AccessLogAspect {

    private final AccessLogService accessLogService;
    private final UserRepository userRepository;

    public AccessLogAspect(AccessLogService accessLogService, UserRepository userRepositoryl, UserRepository userRepository) {
        this.accessLogService = accessLogService;

        this.userRepository = userRepository;
    }

    // ✅ Controller sınıflarındaki tüm public methodları hedef al
    @Pointcut("execution(public * com.hastanerandevu.app.controller..*(..))")
    public void controllerMethods() {}

    // ✅ Başarılı işlem sonrası log kaydı
    @AfterReturning("controllerMethods()")
    public void logSuccess(JoinPoint joinPoint) {
        saveLog(joinPoint, "BAŞARILI", null);
    }

    // ✅ Hata fırlatıldığında log kaydı
    @AfterThrowing(pointcut = "controllerMethods()", throwing = "ex")
    public void logError(JoinPoint joinPoint, Throwable ex) {
        saveLog(joinPoint, "HATA", ex.getMessage());
    }

    @Transactional
    public void saveLog(JoinPoint joinPoint, String status, String errorMessage) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return;

        HttpServletRequest request = attributes.getRequest();

        String email = SecurityUtil.getCurrentUserId();
        if ("anonymousUser".equals(email)) {
            return; // Token yoksa loglama yapılmaz
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + email));

        String userEmail = user.getEmail();
        String role = user.getRole().name();

        String httpMethod = request.getMethod();
        String endpoint = request.getRequestURI();
        String entity = extractEntityName(joinPoint);
        String actionType = extractActionType(httpMethod);

        AccessLog log = new AccessLog();
        log.setTimestamp(LocalDateTime.now());
        log.setUserEmail(userEmail);
        log.setRole(role);
        log.setEndpoint(endpoint);
        log.setMethod(httpMethod);
        log.setEntity(entity);
        log.setActionType(actionType);
        log.setStatus(status);
        log.setErrorMessage(errorMessage);

        // ✅ Eski logları sil (1 haftadan eski olanları)
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        accessLogService.deleteLogsBefore(oneWeekAgo);

        // ✅ Yeni logu kaydet
        accessLogService.saveLog(log);
    }



    private String extractEntityName(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        return className.replace("Controller", "");  // Örn: AppointmentController → Appointment
    }

    private String extractActionType(String httpMethod) {
        return switch (httpMethod.toUpperCase()) {
            case "GET" -> "READ";
            case "POST" -> "CREATE";
            case "PUT", "PATCH" -> "UPDATE";
            case "DELETE" -> "DELETE";
            default -> "OTHER";
        };
    }
}
