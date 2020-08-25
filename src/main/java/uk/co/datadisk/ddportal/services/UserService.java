package uk.co.datadisk.ddportal.services;

import uk.co.datadisk.ddportal.domain.User;
import uk.co.datadisk.ddportal.exceptions.domain.EmailExistException;
import uk.co.datadisk.ddportal.exceptions.domain.UsernameExistException;

import java.util.List;

public interface UserService {

    User register(String firstName, String lastName, String username, String email) throws UsernameExistException, EmailExistException;

    List<User> getUsers();

    User findUserByUsername(String username);
    User findUserByEmail(String email);
}
