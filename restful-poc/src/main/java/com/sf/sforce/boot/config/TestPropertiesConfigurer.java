package com.sf.sforce.boot.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
public class TestPropertiesConfigurer {
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer config = new PropertySourcesPlaceholderConfigurer();
		List<Resource> locations = new ArrayList<Resource>();
		locations.add(new ClassPathResource("test.properties"));
		config.setLocations(locations.toArray(new Resource[locations.size()]));
		return config;
	}
}