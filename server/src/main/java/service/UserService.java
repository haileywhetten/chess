package service;

import dataaccess.DataAccess;
import model.AuthData;
import model.UserData;

import java.util.UUID;
//TODO: Delete other UserData from the datamodel package because I duplicated that

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    public void clear() {
        dataAccess.clear();
    }

    public AuthData register(UserData user) throws Exception {
        if(user.password() == null || user.username() == null || user.email() == null) {
            throw new Exception("bad request");
        }
        if(dataAccess.getUser(user.username()) != null){
            throw new Exception("already taken");
        }
        dataAccess.createUser(user);
        return new AuthData(user.username(), generateToken());
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
