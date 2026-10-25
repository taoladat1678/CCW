package com.example.ShopGiayTheThao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ShopGiayTheThao.entity.Size;

@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {
}
