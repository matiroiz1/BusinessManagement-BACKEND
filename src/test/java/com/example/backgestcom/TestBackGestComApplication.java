package com.example.backgestcom;

import org.springframework.boot.SpringApplication;

public class TestBackGestComApplication {

	public static void main(String[] args) {
		SpringApplication.from(BackGestComApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
