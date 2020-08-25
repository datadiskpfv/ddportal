package uk.co.datadisk.ddportal.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.datadisk.ddportal.domain.User;
import uk.co.datadisk.ddportal.domain.UserPrincipal;
import uk.co.datadisk.ddportal.repositories.UserRepository;
import uk.co.datadisk.ddportal.services.UserService;

import java.util.Date;

@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

    private UserRepository userRepository;
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findUserByUsername(username);

        if (user == null) {
            LOGGER.error("User not found by username: " + username);
            throw new UsernameNotFoundException("User not found by username: " + username);
        } else {
            // update login dates and save user in db
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);

            // UserPrincipal has UserDetails
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info("Returning found user by username: " + username);

            return userPrincipal;
        }
    }
}
