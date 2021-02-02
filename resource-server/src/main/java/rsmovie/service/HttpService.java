package rsmovie.service;

import rsmovie.entity.User;
import rsmovie.service.auth.AuthResponse;

public interface HttpService {

    int registerUser(User user, String password);
    AuthResponse authenticateUser(String username, String password);
}
