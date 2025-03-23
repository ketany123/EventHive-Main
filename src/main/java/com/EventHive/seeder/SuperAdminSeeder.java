package com.EventHive.seeder;


import com.EventHive.entity.User;
import com.EventHive.enums.Role;
import com.EventHive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SuperAdminSeeder implements ApplicationListener<ContextRefreshedEvent> {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public SuperAdminSeeder(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        this.loadSuperAdminUser();
    }

    private void loadSuperAdminUser() {
        User superAdmin = User.builder()
                .role(Role.ROLE_SUPER_ADMIN)
                .username("superUser")
                .password(bCryptPasswordEncoder.encode("superPassword"))
                .firstName("superUserFirstname")
                .lastName("superUserLastname")
                .email("superUserEmail")
                .build();

        if(userRepository.findByUsername(superAdmin.getUsername()).isEmpty()){
            userRepository.save(superAdmin);
        }
    }

}