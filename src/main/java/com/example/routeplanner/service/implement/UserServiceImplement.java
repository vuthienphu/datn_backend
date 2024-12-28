package com.example.routeplanner.service.implement;

import com.example.routeplanner.model.Config;
import com.example.routeplanner.model.InfoUsersDTO;
import com.example.routeplanner.model.Users;
import com.example.routeplanner.repository.UsersRepository;
import com.example.routeplanner.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImplement implements UserService {
    @Autowired
    private UsersRepository usersRepository;


    @Override
    public UserDetailsService userDetailsService(){
        return  new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
                return usersRepository.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
        };
    }

    @Override
    public List<InfoUsersDTO> getInfoUser() {
        return usersRepository.findInfoUser();
    }

    @Override
    public Users updateAuthoritiesUserById(Users user, Integer id) {
        Optional<Users> userData = usersRepository.findById(id);

        if (userData.isPresent()) {
           Users updateUsers= userData.get();
           updateUsers.setRole(user.getRole());
            return usersRepository.save(updateUsers);
        }
        else{
            throw new EntityNotFoundException("Users not found");
        }
    }


    @Override
    public void deleteUserById(Integer id) {
        Optional<Users> userData = usersRepository.findById(id);
        if (userData.isPresent()) {
           usersRepository.deleteById(userData.get().getId());
        }
        else {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }
    }

}
