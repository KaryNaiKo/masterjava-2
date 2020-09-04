package ru.javaops.masterjava.persist.dao;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.*;
import ru.javaops.masterjava.persist.model.User;

import java.util.List;

public interface AbstractUserDao extends AbstractDao {
    @SqlUpdate("INSERT INTO users (full_name, email, flag) VALUES (:fullName, :email, CAST(:flag AS user_flag)) ")
    @GetGeneratedKeys
    int insertGeneratedId(@BindBean User user);

    @SqlUpdate("INSERT INTO users (id, full_name, email, flag) VALUES (:id, :fullName, :email, CAST(:flag AS user_flag)) ")
    void insertWitId(@BindBean User user);

    @SqlQuery("SELECT * FROM users ORDER BY full_name, email LIMIT :it")
    @RegisterBeanMapper(User.class)
    List<User> getWithLimit(@Bind("it") int limit);

    //   http://stackoverflow.com/questions/13223820/postgresql-delete-all-content
    @SqlUpdate("TRUNCATE users")
    @Override
    void clean();

    @SqlBatch("insert into users (full_name, email, flag) VALUES (:fullName, :email, CAST(:flag AS user_flag))")
    @GetGeneratedKeys
    int[] insertAll(@BatchChunkSize int size, @BindBean List<User> users);
}
