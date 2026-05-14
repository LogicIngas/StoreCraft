package za.ac.cput.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.entity.user.User;
import za.ac.cput.service.impl.user.UserServiceImpl;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    //    CRUD

    @PostMapping("/create")
    public User create(@RequestBody User user){
        return this.userService.create(user);
    }

    @PostMapping("/read/{id}")
    public User read(@PathVariable String id){
        return this.userService.read(id);
    }

    @PostMapping("/update")
    public User update(@RequestBody User user){
        return this.userService.update(user);
    }

    @PostMapping("/delete/{id}")
    public boolean delete(@PathVariable String id){
        return this.userService.delete(id);
    }
}
