package za.ac.cput.controller.user;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import za.ac.cput.entity.user.Role;
import za.ac.cput.factory.user.RoleFactory;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RoleControllerTest {

    private Role role = RoleFactory.createRole("Buyer");

     private String BASE_URL = "http://localhost:8080/role";

     @Autowired
     private TestRestTemplate restTemplate;

//    @BeforeEach
//    void setUp() {
//    }

    @Test
    @Order(1)
    void create() {
        String URL = BASE_URL + "/create";
        ResponseEntity<Role> response = restTemplate.postForEntity(URL, role, Role.class);
        Role role = response.getBody();
        assertNotNull(role);
//        assertNotNull(role.getId());
//        assertNotNull(role.getName());
        System.out.println(role);
    }

    @Test
    void read() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}