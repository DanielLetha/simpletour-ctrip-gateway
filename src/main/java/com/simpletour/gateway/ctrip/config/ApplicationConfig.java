package com.simpletour.gateway.ctrip.config;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.web.filter.RequestContextFilter;

/**
 * jersey应用服务器配置类
 * <p>
 * 用于装载、注册各模块的Resource，各种filter、mapper等
 * <p>
 * Created by songfujie on 15/10/24.
 */
public class ApplicationConfig extends ResourceConfig {

    public ApplicationConfig() {
        /**
         * 配置各模块的包路径供jersey container扫描
         */
        packages("com.simpletour.gateway.ctrip.rest",
                "com.simpletour.common.restful.exception",
                "com.simpletour.common.restful.service");
        /**
         *Spring filter to provide a bridge between JAX-RS and Spring request attributes
         */
        register(RequestContextFilter.class);
        /**
         *jersey采用jacksonFeature进行object-json映射
         */
//        register(JacksonFeature.class);
    }
}