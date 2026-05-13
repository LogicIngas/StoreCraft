package za.ac.cput.repository.address;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.entity.address.Address;

@Repository
public interface IAddressRepository extends JpaRepository<Address, String> {
}
