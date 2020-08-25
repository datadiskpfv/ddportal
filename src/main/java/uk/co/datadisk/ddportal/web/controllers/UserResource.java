package uk.co.datadisk.ddportal.web.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.datadisk.ddportal.exceptions.GlobalExceptionHandler;
import uk.co.datadisk.ddportal.exceptions.domain.UserNotFoundException;

@RestController
@RequestMapping(path = {"/", "/user"})
public class UserResource extends GlobalExceptionHandler {

    @GetMapping("/home")
    public String showUser() {
        return "Hello World!";
    }
}
