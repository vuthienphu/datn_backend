package com.example.routeplanner.repository;

import com.example.routeplanner.model.InfoUsersDTO;
import com.example.routeplanner.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByEmail(String email);

    @Query("SELECT new com.example.routeplanner.model.InfoUsersDTO(u.Id, u.fullName, u.email, u.phoneNumber, u.role) FROM Users u")
    List<InfoUsersDTO> findInfoUser();
}
