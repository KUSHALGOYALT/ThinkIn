package com.example.service;
import com.example.model.User;
import com.example.model.UserPrincipal;
import com.example.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        User user = userRepo.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + loginId));

        return new UserPrincipal(user);
    }
}
