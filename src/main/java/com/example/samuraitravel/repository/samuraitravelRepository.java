package com.example.samuraitravel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.samuraitravel.model.samuraitravel;

public interface samuraitravelRepository extends JpaRepository<samuraitravel, Long> {
    List<samuraitravel> findByActiveTrue();
    
    List<samuraitravel> findByPrefecture(String prefecture);
    
    List<samuraitravel> findByCity(String city);
    
    @Query("SELECT r FROM samuraitravel r WHERE " +
           "r.active = true AND " +
           "(:prefecture IS NULL OR r.prefecture = :prefecture) AND " +
           "(:city IS NULL OR r.city = :city) AND " +
           "r.name LIKE %:keyword%")
    List<samuraitravel> searchsamuraitravels(
        @Param("prefecture") String prefecture,
        @Param("city") String city,
        @Param("keyword") String keyword
    );
}
