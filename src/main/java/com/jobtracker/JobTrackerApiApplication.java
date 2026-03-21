package com.jobtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class JobTrackerApiApplication {

	public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        SpringApplication.run(JobTrackerApiApplication.class, args);
	}

}
