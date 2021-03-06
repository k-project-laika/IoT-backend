package com.laika.IoT.repository;

import com.laika.IoT.entity.Home;
import com.laika.IoT.entity.Manager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface HomeRepository extends JpaRepository<Home, Long> {
    Home findByAddress(String address);
    Page<Home> findByManager(Manager manager, Pageable pageable);
}
