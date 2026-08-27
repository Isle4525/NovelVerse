package com.novelverse.novelverse.service;


import com.novelverse.novelverse.domain.User;
import com.novelverse.novelverse.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void register (String username, String password){
        User user = new User();
        user.setName(username);
        user.setPassword(password);

        userRepository.save(user);
    }


    public User login (String username, String password){

        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        if(!user.getPassword().equals(password)){
            throw new RuntimeException("Wrong password");
        }

        return user;
    }
}
