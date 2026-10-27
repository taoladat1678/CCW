package com.example.ShopGiayTheThao.repository;



import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ShopGiayTheThao.entity.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {


    List<Brand> findAllByOrderByIdAsc();

}
