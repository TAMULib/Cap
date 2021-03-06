package edu.tamu.cap.auth.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import edu.tamu.cap.utility.MockUserUtility;
import edu.tamu.cap.model.Role;
import edu.tamu.cap.model.User;
import edu.tamu.weaver.auth.model.Credentials;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public final class CustomUserDetailsTest {

    @Autowired
    private MockUserUtility mockUserUtility;

    @Test
    public void testNewAppUserDetails() throws JsonParseException, JsonMappingException, IOException {
        Credentials credentials = mockUserUtility.getMockAggieJackCredentials();
        User user = new User(credentials.getUin(), credentials.getFirstName(), credentials.getLastName(), credentials.getRole());
        CustomUserDetails userDetails = new CustomUserDetails(user);
        assertEquals(credentials.getLastName(), userDetails.getLastName(), "App user details had the incorrect last name!");
        assertEquals(credentials.getFirstName(), userDetails.getFirstName(), "App user details had the incorrect first name!");
        assertEquals(credentials.getUin(), userDetails.getUsername(), "App user details had the incorrect username!");
        assertEquals(Role.valueOf(credentials.getRole()), userDetails.getRole(), "App user details had the incorrect role!");
    }

}
