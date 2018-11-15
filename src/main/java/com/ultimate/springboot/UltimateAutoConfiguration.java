package com.ultimate.springboot;

import com.ultimate.core.DbUltimate;
import com.ultimate.db.config.DbConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(DbUltimate.class)
@EnableConfigurationProperties(DbConfig.class)
public class UltimateAutoConfiguration{

    @Autowired
    private DbConfig dbConfig;

    @Bean
    public DbUltimate createUltimate(){

        return new DbUltimate(dbConfig);
    }

}
