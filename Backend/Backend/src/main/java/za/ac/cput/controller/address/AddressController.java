package za.ac.cput.controller.address;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.entity.address.Address;
import za.ac.cput.service.impl.address.AddressServiceImpl;

@RestController
@RequestMapping("/address")
public class AddressController {

    private final AddressServiceImpl addressService;

    @Autowired
    public AddressController(AddressServiceImpl addressService) {
        this.addressService = addressService;
    }

    @PostMapping("/create")
    public Address create(@RequestBody Address address) {
        return addressService.create(address);
    }

    @GetMapping("/read/{id}")
    public Address read(@PathVariable String id) {
        return addressService.read(id);
    }

    @PutMapping("/update")
    public Address update(@RequestBody Address address) {
        return addressService.update(address);
    }

    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable String id) {
        return addressService.delete(id);
    }
}