package com.example.store.config;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class EndpointsListener {

    @Value("${com.example.store.list-endpoints:false}")
    private boolean listEndpoints;

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        if (listEndpoints) {
            getHandlerMethods(event).forEach((key, value) -> {
                String httpMethod = formatMethodName(key);
                String httpPath = formatMethodPath(key);

                if (httpMethod.trim().length() != 0) {
                    MDC.put("requestId", "registered-api");
                    log.debug("EndpointsListener_handleContextRefresh : {} {}", httpMethod, httpPath);
                    MDC.remove("requestId");
                }
            });
        }
    }

    private Map<RequestMappingInfo, HandlerMethod> getHandlerMethods(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();

        RequestMappingHandlerMapping requestMappingHandlerMapping =
                applicationContext.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);

        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();

        return handlerMethods.entrySet().stream()
                .sorted(Comparator.comparing(entry -> formatMethodPath(entry.getKey())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    private String formatMethodName(RequestMappingInfo key) {
        return String.format(
                "%1$6s", key.getMethodsCondition().toString().replace("[", "").replace("]", ""));
    }

    private String formatMethodPath(RequestMappingInfo key) {
        PathPatternsRequestCondition pathPatternsRequestCondition = key.getPathPatternsCondition();

        if (pathPatternsRequestCondition == null || pathPatternsRequestCondition.isEmpty()) {
            return "";
        } else {
            return pathPatternsRequestCondition.toString().replace("[", "").replace("]", "");
        }
    }
}
