package service;

import model.AuthData;
import model.UserData;

import java.util.UUID;
//TODO: Delete other UserData from the datamodel package because I duplicated that

public class UserService {
    public AuthData register(UserData user) {
        return new AuthData(user.username(), generateToken());
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
