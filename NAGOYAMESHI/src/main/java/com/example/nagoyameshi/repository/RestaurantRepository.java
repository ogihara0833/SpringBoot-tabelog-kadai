package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.nagoyameshi.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

    @Query("""
        SELECT r FROM Restaurant r
        WHERE (:keyword IS NULL OR :keyword = '' OR
               LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               LOWER(r.address) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:categoryId IS NULL OR r.category.id = :categoryId)
          AND (:maxBudget IS NULL OR (
               (r.lunchPriceMin IS NOT NULL AND r.lunchPriceMin <= :maxBudget)
               OR
               (r.dinnerPriceMin IS NOT NULL AND r.dinnerPriceMin <= :maxBudget)
          ))
          AND (:area IS NULL OR :area = '' OR LOWER(r.address) LIKE LOWER(CONCAT('%', :area, '%')))
    """)
    Page<Restaurant> searchAll(
        @Param("keyword") String keyword,
        @Param("categoryId") Integer categoryId,
        @Param("maxBudget") Integer maxBudget,
        @Param("area") String area,
        Pageable pageable
    );

    Page<Restaurant> findAllByOrderByIdAsc(Pageable pageable);

    Page<Restaurant> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    boolean existsByCategoryId(Integer categoryId);

	List<Restaurant> findByIsFeaturedTrueOrderByCreatedAtDesc();
}
