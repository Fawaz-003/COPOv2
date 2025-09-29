package com.copo.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RoleFilter roleFilter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(roleFilter)
                .addPathPatterns("/**")
                .excludePathPatterns("/login/**", "/register/**", "/css/**", "/js/**", "/images/**"); // Allow login/register/static
    }
}
