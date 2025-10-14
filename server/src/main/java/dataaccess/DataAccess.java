package dataaccess;

import model.*;

public interface DataAccess {
    void clear();
    void createUser(UserData user);
    UserData getUser(String username);
}
