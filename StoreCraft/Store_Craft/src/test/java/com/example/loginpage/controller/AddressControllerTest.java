package com.example.loginpage.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.*;

import com.example.loginpage.model.Address;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AddressControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String BASE_URL = "http://localhost:";

    private static String addressId;
    private static final String USER_ID = "testUser123";

    @Test
    @Order(1)
    void create() {
        String url = BASE_URL + port + "/address/create";

        AddressController.CreateAddressRequest request = new AddressController.CreateAddressRequest();
        request.userId = USER_ID;
        request.type = "SHIPPING";
        request.recipientName = "Inga Mbobo";
        request.phoneNumber = "555-1234";
        request.streetAddress = "100 Test Ave";
        request.city = "Testville";
        request.stateProvince = "TS";
        request.postalCode = "99999";
        request.country = "Testland";
        request.isDefault = true;

        ResponseEntity<Address> response = restTemplate.postForEntity(url, request, Address.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getAddressId());

        addressId = response.getBody().getAddressId();
    }

    @Test
    @Order(2)
    void read() {
        String url = BASE_URL + port + "/address/" + addressId;

        ResponseEntity<Address> response = restTemplate.getForEntity(url, Address.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Inga Mbobo", response.getBody().getRecipientName());
    }

    @Test
    @Order(3)
    void update() {
        String url = BASE_URL + port + "/address/" + addressId;

        AddressController.UpdateAddressRequest request = new AddressController.UpdateAddressRequest();
        request.type = "BILLING";
        request.recipientName = "Inga Smith";
        request.phoneNumber = "555-1234";
        request.streetAddress = "100 Test Ave";
        request.city = "Testville";
        request.stateProvince = "TS";
        request.postalCode = "99999";
        request.country = "Testland";
        request.isDefault = true;

        HttpEntity<AddressController.UpdateAddressRequest> entity = new HttpEntity<>(request);
        ResponseEntity<Address> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Address.class);

        assertNotNull(response.getBody());
    }

    @Test
    @Order(4)
    void delete() {
        String url = BASE_URL + port + "/address/" + addressId;

        restTemplate.delete(url);

        // Verify it's deleted by trying to read it again
        ResponseEntity<Address> response = restTemplate.getForEntity(url, Address.class);

        // Assuming your service returns null or throws an exception which translates to
        // 404
        // Let's just assert that it's either not OK or body is null
        if (response.getStatusCode() == HttpStatus.OK) {
            assertNull(response.getBody());
        } else {
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }
}