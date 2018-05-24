package com.sf.sforce;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.sf.sforce.auth.config.SforceAuthConfig;

@Configuration
@ComponentScan(basePackages = {"com.sf.sforce.*"})
@SpringBootApplication
@EnableScheduling
@Import({ 
		SforceAuthConfig.class
		})
public class CtApplication {


	public static void main(String[] args) {
		SpringApplication.run(CtApplication.class, args);
	}

	public void addedToFixCheckStyle() {

	}

	@PostConstruct
	public void init() {		
	}
}
