package com.example.ShopGiayTheThao.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.example.ShopGiayTheThao.dto.BrandDTO;
import com.example.ShopGiayTheThao.dto.BrandRequest;
import com.example.ShopGiayTheThao.entity.Brand;

public interface BrandService {

    List<BrandDTO> getAllBrands();

    BrandDTO getBrandById(Long id);

    Page<BrandDTO> getBrandsForAdmin(int page, int size, String keyword);

    Page<BrandDTO> getBrandsForAdmin(int page, int size);

    Brand getBrandEntityById(Long id);

    Brand createBrand(BrandRequest request);

    Brand updateBrand(BrandRequest request);

    void deleteBrand(Long id);

    long getTotalBrands();
}
