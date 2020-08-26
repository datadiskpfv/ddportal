package uk.co.datadisk.ddportal.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import uk.co.datadisk.ddportal.domain.User;
import uk.co.datadisk.ddportal.domain.UserPrincipal;
import uk.co.datadisk.ddportal.exceptions.GlobalExceptionHandler;
import uk.co.datadisk.ddportal.exceptions.domain.EmailExistException;
import uk.co.datadisk.ddportal.exceptions.domain.UsernameExistException;
import uk.co.datadisk.ddportal.jwt.JWTTokenProvider;
import uk.co.datadisk.ddportal.services.UserService;

import static org.springframework.http.HttpStatus.OK;
import static uk.co.datadisk.ddportal.constants.SecurityConstant.JWT_TOKEN_HEADER;

@RestController
@RequestMapping(path = {"/", "/user"})
public class UserResource extends GlobalExceptionHandler {

    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JWTTokenProvider jwtTokenProvider;

    @Autowired
    public UserResource(UserService userService, AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/home")
    public String showUser() {
        return "Hello World!";
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws UsernameExistException, EmailExistException {
        User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
        return new ResponseEntity<>(newUser, OK);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) throws UsernameExistException, EmailExistException {
        // This will throw an exception if any issues authenticating
        authenticate(user.getUsername(), user.getPassword());

        User loginUser = userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);

        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);

        return new ResponseEntity<>(loginUser, jwtHeader, OK);
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));

        return headers;
    }

    private void authenticate(String username, String password) {
        // This will throw an exception if any issues authenticating
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
