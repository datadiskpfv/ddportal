package uk.co.datadisk.ddportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;

import static uk.co.datadisk.ddportal.constants.FileConstant.USER_FOLDER;

@SpringBootApplication
public class DdportalApplication {

    public static void main(String[] args) {
        SpringApplication.run(DdportalApplication.class, args);

        // Create the image profile directory for the user
        new File(USER_FOLDER).mkdirs();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(5);
    }

}
