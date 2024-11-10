package com.pth.digital_assignment.repository;

import com.pth.digital_assignment.model.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    @Query(value = """
        SELECT *,
        (6371 * acos(cos(radians(:latitude)) * cos(radians(s.latitude))
        * cos(radians(s.longitude) - radians(:longitude))
        + sin(radians(:latitude)) * sin(radians(s.latitude)))) AS distance
        FROM tbl_shop s
        HAVING distance < :radius
        ORDER BY distance
        """,
          nativeQuery = true)
    Page<Shop> findNearbyShops(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radius") Double radiusInKm,
            Pageable pageable
    );
}
