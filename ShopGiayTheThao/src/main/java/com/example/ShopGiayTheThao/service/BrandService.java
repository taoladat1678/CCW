package com.example.ShopGiayTheThao.service;

import java.util.List;

import com.example.ShopGiayTheThao.dto.BrandDTO;

public interface BrandService {
    List<BrandDTO> getAllBrands();
    BrandDTO getBrandById(Long id);
}
