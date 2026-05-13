package za.ac.cput.service.impl.user;

import org.springframework.stereotype.Service;
import za.ac.cput.entity.user.User;
import za.ac.cput.repository.user.IUserRepository;
import za.ac.cput.service.user.IUserService;

@Service
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;

    public UserServiceImpl(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public User read(String id) {
        return this.userRepository.findById(id).orElse(null);
    }

    @Override
    public User update(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public boolean delete(String id) {
        this.userRepository.deleteById(id);
        return true;
    }
}
