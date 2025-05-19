package com.example.samuraitravel;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class SamuraitravelApplication {

	public static void main(String[] args) {
		// 画像アップロードディレクトリを作成
		String uploadDir = "src/main/resources/static/images/houses";
		File directory = new File(uploadDir);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		SpringApplication.run(SamuraitravelApplication.class, args);
	}
}
