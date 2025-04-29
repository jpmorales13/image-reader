package com.juan.img.reader;

import com.juan.img.reader.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class ImageReaderApplication {
	public static void main(String[] args) {
		SpringApplication.run(ImageReaderApplication.class, args);
	}
}
