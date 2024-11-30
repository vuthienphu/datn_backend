package com.example.routeplanner.repository;

import com.example.routeplanner.model.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepository extends JpaRepository<Config, Integer> {

    @Query("SELECT c.value FROM Config c WHERE c.configId = :configId")
    Double findConfig(@Param("configId") String configId);
    @Query("SELECT c.status FROM Config c WHERE c.configId = :configId")
    Boolean findConfigStatus(@Param("configId") String configId);
}
