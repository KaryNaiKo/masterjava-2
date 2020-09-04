package ru.javaops.masterjava.persist.dao;

import ru.javaops.masterjava.persist.model.User;

public abstract class UserDao implements AbstractUserDao {

    public User insert(User user) {
        if (user.isNew()) {
            int id = insertGeneratedId(user);
            user.setId(id);
        } else {
            insertWitId(user);
        }
        return user;
    }
}
