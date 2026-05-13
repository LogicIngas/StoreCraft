package za.ac.cput.service.impl.address;

import org.springframework.stereotype.Service;
import za.ac.cput.entity.address.Address;
import za.ac.cput.repository.address.IAddressRepository;
import za.ac.cput.service.address.IAddressService;

@Service
public class AddressServiceImpl implements IAddressService {
    private final IAddressRepository addressRepository;

    public AddressServiceImpl(IAddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public Address create(Address address) {
        return addressRepository.save(address);
    }

    @Override
    public Address read(String id) {
        return addressRepository.findById(id).orElse(null);
    }

    @Override
    public Address update(Address address) {
        return addressRepository.save(address);
    }

    @Override
    public boolean delete(String id) {
        this.addressRepository.deleteById(id);
        return true;
    }
}
