package com.spring.repository;

import com.spring.model.FilterUrl;
import com.spring.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilterRespository extends CrudRepository<FilterUrl, Integer> {

    @Query(value = "SELECT * FROM filter WHERE url=:url AND user_id=:user_id", nativeQuery = true)
    FilterUrl getUserFilterById(@Param("url") String url, @Param("user_id") long user_id);

    @Query(value = "SELECT * FROM filter WHERE user_id=:user_id", nativeQuery = true)
    List<FilterUrl> getFilterByUserId(@Param("user_id") long user_id);
}
