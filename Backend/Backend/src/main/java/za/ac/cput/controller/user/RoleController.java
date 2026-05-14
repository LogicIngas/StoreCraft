package za.ac.cput.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.entity.user.Role;
import za.ac.cput.service.impl.address.AddressServiceImpl;
import za.ac.cput.service.impl.user.RoleServiceImpl;

@RestController
@RequestMapping("/role")
public class RoleController {
    private final RoleServiceImpl roleService;

    @Autowired
    public RoleController(RoleServiceImpl roleService) {
        this.roleService = roleService;
    }


    @PostMapping("/create")
    public Role create(@RequestBody Role role){
        return this.roleService.create(role);
    }

    @GetMapping("/read/{id}")
    public Role read(@PathVariable String id){
        return this.roleService.read(id);
    }

    @PutMapping("/update")
    public Role update(@RequestBody Role role){
        return this.roleService.update(role);
    }
    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable String id){
        return this.roleService.delete(id);
    }
}
