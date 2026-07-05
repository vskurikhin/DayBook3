/*
 * This file was last modified at 2026.05.07 14:57 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * ApiLoggingFilterConfiguration.java
 * $Id$
 */

package su.svn.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import su.svn.core.filters.ApiLoggingFilter;

@Component
@ConditionalOnExpression("${app.api.logging.enable:true}")
public class ApiLoggingFilterConfiguration {
    @Value("${app.api.logging.url-patterns:*}")
    private String[] urlPatterns;

    @Value("${app.api.logging.request-id-header-name:x-request-id}")
    private String requestIdParamName;

    @Bean
    public FilterRegistrationBean<ApiLoggingFilter> loggingFilter() {
        FilterRegistrationBean<ApiLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ApiLoggingFilter(requestIdParamName.toLowerCase()));
        registrationBean.addUrlPatterns(urlPatterns);
        return registrationBean;
    }
}
