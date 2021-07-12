package com.saltlux.khnp.searcher.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Around("within(com.saltlux.khnp..*Service)")
    public Object logging(ProceedingJoinPoint pjp) throws Throwable {
        String params = getRequestParams();
        long startAt = System.currentTimeMillis();
        log.debug("----------> REQUEST : {}({}) = {}", pjp.getSignature().getDeclaringTypeName(), pjp.getSignature().getName(), params);
        Object result = null;
        try {
            result = pjp.proceed();
            return result;
        } catch (Throwable throwable) {
            throw throwable;
        } finally {
            long endAt = System.currentTimeMillis();
            log.debug("----------> RESPONSE : {}({}) = {} ({}ms)", pjp.getSignature().getDeclaringTypeName(), pjp.getSignature().getName(), result, endAt-startAt);
        }
    }

    private String getRequestParams() {

        StringBuffer params = new StringBuffer();

        RequestAttributes requestAttribute = RequestContextHolder.getRequestAttributes();

        if(requestAttribute != null){
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes()).getRequest();

            Map<String, String[]> paramMap = request.getParameterMap();

            if(!paramMap.isEmpty()) {
                params.append(" [")
                        .append(paramMapToString(paramMap))
                        .append("]");
            }
        }
        return params.toString();
    }

    private String paramMapToString(Map<String, String[]> paramMap) {
        return paramMap.entrySet().stream()
                .map(entry -> String.format("%s -> (%s)",
                        entry.getKey(), String.join(",", entry.getValue())))
                .collect(Collectors.joining(", "));
    }
}