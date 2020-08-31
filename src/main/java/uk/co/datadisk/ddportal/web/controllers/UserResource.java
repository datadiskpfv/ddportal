package uk.co.datadisk.ddportal.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.co.datadisk.ddportal.domain.HttpResponse;
import uk.co.datadisk.ddportal.domain.User;
import uk.co.datadisk.ddportal.domain.UserPrincipal;
import uk.co.datadisk.ddportal.exceptions.GlobalExceptionHandler;
import uk.co.datadisk.ddportal.exceptions.domain.EmailExistException;
import uk.co.datadisk.ddportal.exceptions.domain.EmailNotFoundException;
import uk.co.datadisk.ddportal.exceptions.domain.NotAnImageFileException;
import uk.co.datadisk.ddportal.exceptions.domain.UsernameExistException;
import uk.co.datadisk.ddportal.jwt.JWTTokenProvider;
import uk.co.datadisk.ddportal.services.UserService;

import javax.mail.MessagingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static uk.co.datadisk.ddportal.constants.FileConstant.*;
import static uk.co.datadisk.ddportal.constants.SecurityConstant.JWT_TOKEN_HEADER;

@RestController
@RequestMapping(path = {"/", "/user"})
public class UserResource extends GlobalExceptionHandler {

    public static final String EMAIL_SENT  = "An email with a new password was sent to ";
    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";

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
    public ResponseEntity<User> login(@RequestBody User user) {
        // This will throw an exception if any issues authenticating
        authenticate(user.getUsername(), user.getPassword());

        User loginUser = userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);

        HttpHeaders jwtHeader = createJwtHeader(userPrincipal);

        return new ResponseEntity<>(loginUser, jwtHeader, OK);
    }

    @PostMapping("/add")
    public ResponseEntity<User> addUser(@RequestParam("firstName") String firstName,
                                        @RequestParam("lastName") String lastName,
                                        @RequestParam("username") String username,
                                        @RequestParam("email") String email,
                                        @RequestParam("role") String role,
                                        @RequestParam("isActive") String isActive,
                                        @RequestParam("isNonLocked") String isNonLocked,
                                        @RequestParam( value = "profileImage", required = false) MultipartFile profileImage)
            throws UsernameExistException, EmailExistException, IOException, NotAnImageFileException {

        User newUser = userService.addNewUser(firstName, lastName, username, email,
                role, Boolean.parseBoolean(isActive), Boolean.parseBoolean(isNonLocked), profileImage);

        return new ResponseEntity<>(newUser, OK);
    }

    @PostMapping("/update")
    public ResponseEntity<User> updateUser(@RequestParam("currentUsername") String currentUsername,
                                        @RequestParam("firstName") String firstName,
                                        @RequestParam("lastName") String lastName,
                                        @RequestParam("username") String username,
                                        @RequestParam("email") String email,
                                        @RequestParam("role") String role,
                                        @RequestParam("isActive") String isActive,
                                        @RequestParam("isNonLocked") String isNonLocked,
                                        @RequestParam( value = "profileImage", required = false) MultipartFile profileImage)
            throws UsernameExistException, EmailExistException, IOException, NotAnImageFileException {

        User updatedUser = userService.updateUser(currentUsername, firstName, lastName, username, email,
                role, Boolean.parseBoolean(isActive), Boolean.parseBoolean(isNonLocked), profileImage);

        return new ResponseEntity<>(updatedUser, OK);
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<User> updateProfileImage(@RequestParam("username") String username,
                                           @RequestParam( value = "profileImage") MultipartFile profileImage)
            throws UsernameExistException, EmailExistException, IOException, NotAnImageFileException {

        User user = userService.updateProfileImage(username,profileImage);
        return new ResponseEntity<>(user, OK);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<User> findUser(@PathVariable("username") String username) {
        User user = userService.findUserByUsername(username);

        if (user == null) {
            return new ResponseEntity<>(null, NO_CONTENT);
        }
        return new ResponseEntity<>(user, OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, OK);
    }

    @GetMapping("/reset-password/{email}")
    public ResponseEntity<HttpResponse> getAllUsers(@PathVariable("email") String email) throws EmailNotFoundException, MessagingException {
        System.out.println("Resetting password for " + email);
        userService.resetPassword(email);
        return response(OK, EMAIL_SENT + email);
    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username) throws IOException {
        userService.deleteUser(username);
        return response(OK, USER_DELETED_SUCCESSFULLY);
    }

    @GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username,
                                  @PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
    }

    @GetMapping(path = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // try with resource block
        try(InputStream inputStream = url.openStream()) {
            int bytesRead;
            byte[] chunk = new byte[1024];

            while((bytesRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus,
                httpStatus.getReasonPhrase().toUpperCase(), message), httpStatus);
    }

    private HttpHeaders createJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
        return headers;
    }

    private void authenticate(String username, String password) {
        // This will throw an exception if any issues authenticating
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
