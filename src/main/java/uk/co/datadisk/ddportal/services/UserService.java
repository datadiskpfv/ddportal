package uk.co.datadisk.ddportal.services;

import org.springframework.web.multipart.MultipartFile;
import uk.co.datadisk.ddportal.domain.User;
import uk.co.datadisk.ddportal.exceptions.domain.EmailExistException;
import uk.co.datadisk.ddportal.exceptions.domain.UsernameExistException;

import java.util.List;

public interface UserService {

    User register(String firstName, String lastName, String username, String email) throws UsernameExistException, EmailExistException;

    List<User> getUsers();

    User findUserByUsername(String username);
    User findUserByEmail(String email);

    User addNewUser(String firstName, String lastName, String username, String email,
                    String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UsernameExistException, EmailExistException;

    User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail,
                    String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage);

    void deleteUser(Long id);

    void resetPassword(String email);

    User updateProfileImage(String username, MultipartFile profileImage);
}
