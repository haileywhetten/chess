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

    public AuthData login(UserData user) throws Exception {
        if(user.username() == null || user.password() == null) {
            throw new Exception("bad request");
        }
        if(dataAccess.getUser(user.username()) == null || !dataAccess.getUser(user.username()).password().equals(user.password())) {
            throw new Exception("unauthorized");
        }

        var authData = new AuthData(user.username(), generateToken());
        dataAccess.createAuth(authData);
        return authData;
    }


    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
