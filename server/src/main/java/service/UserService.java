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
        AuthData authData = new AuthData(user.username(), generateToken());
        dataAccess.createUser(user);
        dataAccess.createAuth(authData);
        //return new AuthData(user.username(), generateToken());
        return authData;
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

    public void logout(String authToken) throws Exception {
        AuthData authData = dataAccess.getAuth(authToken);
        if(authData == null) {
            throw new Exception("unauthorized");
        }
        dataAccess.deleteAuth(authData);
    }


    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
