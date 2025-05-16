package com.example.samuraitravel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.model.samuraitravelImage;

public interface samuraitravelImageRepository extends JpaRepository<samuraitravelImage, Long> {
    List<samuraitravelImage> findBysamuraitravelIdOrderByDisplayOrder(Long samuraitravelId);
}
