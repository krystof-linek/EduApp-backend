package cz.mendelu.xlinek.eduapp;

import cz.mendelu.xlinek.eduapp.api.course.content.ContentType;
import cz.mendelu.xlinek.eduapp.api.course.content.ContentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EduBackendApp {

	public static void main(String[] args) {

		SpringApplication.run(EduBackendApp.class, args);


	}

}
