package za.ac.cput.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.entity.user.Role;

@Repository
public interface IRoleRepository extends JpaRepository<Role, String> {
}
