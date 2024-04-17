package com.bstu.UniversityIIT.repository;

import com.bstu.UniversityIIT.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    @Query("""
            SELECT u FROM User u WHERE u.role = 'TEACHER'
            """)
    List<User> findAllTeachers();
}
