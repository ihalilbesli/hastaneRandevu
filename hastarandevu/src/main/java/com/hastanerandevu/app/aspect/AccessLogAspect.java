package com.hastanerandevu.app.aspect;

import com.hastanerandevu.app.model.AccessLog;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.AccessLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.hastanerandevu.app.util.SecurityUtil;

import java.time.LocalDateTime;

@Aspect
@Component
public class AccessLogAspect {
    private final AccessLogService accessLogService;
    private final UserRepository userRepository;

    public AccessLogAspect(AccessLogService accessLogService, UserRepository userRepository) {
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

        String httpMethod = request.getMethod();
        String endpoint = request.getRequestURI();
        String entity = extractEntityName(joinPoint);
        String actionType = extractActionType(httpMethod);

        User user = SecurityUtil.getCurrentUser(userRepository);
        String userEmail = user.getEmail();
        String role = user.getRole().name();

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
