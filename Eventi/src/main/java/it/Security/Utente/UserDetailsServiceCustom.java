package it.Security.Utente;

import it.Entities.Utente.UserEntity;
import it.Repositories.db.Utente.User_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserDetailsServiceCustom implements UserDetailsService { //voglio creare un servizio che mi restituisca un utente, ma non uso quella di spring, la personalizzo
    private final User_Repository userRepository;
    @Autowired
    public UserDetailsServiceCustom(User_Repository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findUserEntityByEmail(email); //.orElseThrow(() -> new UsernameNotFoundException("Username non valido"));
        if(user.getStatus()) {
            List<String> roles = user.getUserRoles();
            Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
            for (String authority : roles) {
                grantedAuthorities.add(new SimpleGrantedAuthority(authority));
            }
            return new User(user.getEmail(), user.getPassword(), grantedAuthorities);
        }
        return null;
    }

}
