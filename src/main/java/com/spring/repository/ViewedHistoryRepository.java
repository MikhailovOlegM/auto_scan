package com.spring.repository;

import com.spring.model.User;
import com.spring.model.ViewedHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViewedHistoryRepository extends CrudRepository<ViewedHistory, Integer> {
}
