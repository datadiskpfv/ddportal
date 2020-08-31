package uk.co.datadisk.ddportal.services;

import org.springframework.web.multipart.MultipartFile;
import uk.co.datadisk.ddportal.domain.User;
import uk.co.datadisk.ddportal.exceptions.domain.EmailExistException;
import uk.co.datadisk.ddportal.exceptions.domain.EmailNotFoundException;
import uk.co.datadisk.ddportal.exceptions.domain.NotAnImageFileException;
import uk.co.datadisk.ddportal.exceptions.domain.UsernameExistException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface UserService {

    User register(String firstName, String lastName, String username, String email) throws UsernameExistException, EmailExistException;

    List<User> getUsers();

    User findUserByUsername(String username);
    User findUserByEmail(String email);

    User addNewUser(String firstName, String lastName, String username, String email,
                    String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UsernameExistException, EmailExistException, IOException, NotAnImageFileException;

    User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail,
                    String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UsernameExistException, EmailExistException, IOException, NotAnImageFileException;

    void deleteUser(String username) throws IOException;

    void resetPassword(String email) throws EmailNotFoundException, MessagingException;

    User updateProfileImage(String username, MultipartFile profileImage) throws UsernameExistException, EmailExistException, IOException, NotAnImageFileException;
}
