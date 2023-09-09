package com.intuit.test.model.dao.in_mem.config.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScans({@ComponentScan("com.intuit.test.spring.source_read"),@ComponentScan("com.intuit.test.spring.model.in_mem")})
public class TestConfig {

}
