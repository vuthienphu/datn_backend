package com.example.routeplanner.repository;

import com.example.routeplanner.model.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepository extends JpaRepository<Config, Integer> {
}
