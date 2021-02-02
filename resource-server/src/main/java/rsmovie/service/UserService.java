package rsmovie.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rsmovie.entity.User;
import rsmovie.utility.Recommendations;

import java.util.Optional;

public interface UserService {

    Optional<User> findById(String id);
    Optional<User> findByUsername(String username);
    Page<User> findAll(Pageable pageable);
    User save(User user);
}
