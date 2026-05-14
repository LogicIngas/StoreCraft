package za.ac.cput.service.impl.user;

import org.springframework.stereotype.Service;
import za.ac.cput.entity.user.Role;
import za.ac.cput.repository.user.IRoleRepository;
import za.ac.cput.service.user.IRoleService;
@Service
public class RoleServiceImpl implements IRoleService {
    private final IRoleRepository roleRepository;

    public RoleServiceImpl(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role create(Role role) {
        return this.roleRepository.save(role);
    }

    @Override
    public Role read(String id) {
        return this.roleRepository.findById(id).orElse(null);
    }

    @Override
    public Role update(Role role) {
        return this.roleRepository.save(role);
    }

    @Override
    public boolean delete(String id) {
        this.roleRepository.deleteById(id);
        return true;
    }
}
