package com.user.app.repository;

import com.user.app.entity.Users;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTest {

    @Autowired
    private UsersRepository usersRepository;

    @Test
    public void UserRepositorySaveReturnSavedProdcut(){
        Users test = Users.builder()
                .userName("Antonio")
                .userEmail("antonio@mail.com")
                .userPassword("anton-123")
                .build();
        Users savedUser = usersRepository.save(test);
        Assertions.assertThat(savedUser).isNotNull();
    }

    @Test
    public void testUserRepositoryFindByUserEmail(){
        Users test = Users.builder()
                .userName("Antonio")
                .userEmail("antonio@mail.com")
                .userPassword("anton-123")
                .build();
        Users savedUser = usersRepository.save(test);
        Users found = usersRepository.findByUserEmail("antonio@mail.com");
        Assertions.assertThat(found.getUserEmail()).isEqualTo(savedUser.getUserEmail());
    }

    @Test
    public void testUserRepositoryFindByEmail(){
        Users test = Users.builder()
                .userName("Antonio")
                .userEmail("antonio@mail.com")
                .userPassword("anton-123")
                .build();
        Users savedUser = usersRepository.save(test);
        Users found = usersRepository.findByEmail("antonio@mail.com");
        Assertions.assertThat(found.getUserEmail()).isEqualTo(savedUser.getUserEmail());
    }


}
