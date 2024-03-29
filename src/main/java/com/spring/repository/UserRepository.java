package com.spring.repository;

import com.spring.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    @Query(value = "SELECT * FROM user WHERE chat_id=:chat_id", nativeQuery = true)
    User getUserByChatId(@Param("chat_id") long chatId);
}
