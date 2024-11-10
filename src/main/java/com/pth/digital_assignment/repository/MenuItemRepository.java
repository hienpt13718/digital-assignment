package com.pth.digital_assignment.repository;

import com.pth.digital_assignment.model.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    Page<MenuItem> findByShopId(Long shopId, Pageable pageable);
}
