package com.hejz.runtime_sh_file;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author:hejz 75412985@qq.com
 * @create: 2023-02-10 18:01
 * @Description: RestTemplate配置
 */
@Configuration
public class RestTemplateConfig {
    @Bean
    RestTemplate restTemplage(RestTemplateBuilder builder){
        return builder.build();
    }
}
