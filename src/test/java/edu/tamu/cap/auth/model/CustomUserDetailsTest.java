package edu.tamu.cap.auth.model;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import edu.tamu.cap.model.Role;
import edu.tamu.cap.model.User;
import edu.tamu.weaver.auth.model.Credentials;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public final class CustomUserDetailsTest /* extends AuthMockTests*/ {

    @Test
    public void testNewAppUserDetails() throws JsonParseException, JsonMappingException, IOException {
        Credentials credentials = getMockAggieJackCredentials();
        User user = new User(credentials.getUin(), credentials.getFirstName(), credentials.getLastName(), credentials.getRole());
        CustomUserDetails userDetails = new CustomUserDetails(user);
        assertEquals(credentials.getLastName(), userDetails.getLastName(), "App user details had the incorrect last name!");
        assertEquals(credentials.getFirstName(), userDetails.getFirstName(), "App user details had the incorrect first name!");
        assertEquals(credentials.getUin(), userDetails.getUsername(), "App user details had the incorrect username!");
        assertEquals(Role.valueOf(credentials.getRole()), userDetails.getRole(), "App user details had the incorrect role!");
    }
    protected Credentials getMockAggieJackCredentials() {
        Credentials credentials = new Credentials();
        credentials.setFirstName("Aggie");
        credentials.setLastName("Jack");
        credentials.setUin("123456789");
        credentials.setNetid("aggiejack");
        credentials.setRole("ROLE_ADMIN");
        credentials.setEmail("aggiejack@tamu.edu");
        return credentials;
    }

    protected Credentials getMockAggieJaneCredentials() {
        Credentials credentials = new Credentials();
        credentials.setFirstName("Aggie");
        credentials.setLastName("Jane");
        credentials.setUin("987654321");
        credentials.setNetid("aggiejane");
        credentials.setRole("ROLE_MANAGER");
        credentials.setEmail("aggiejane@tamu.edu");
        return credentials;
    }

}
