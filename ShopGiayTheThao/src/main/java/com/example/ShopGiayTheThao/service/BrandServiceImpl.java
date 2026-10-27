package com.example.ShopGiayTheThao.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ShopGiayTheThao.dto.BrandDTO;
import com.example.ShopGiayTheThao.entity.Brand;
import com.example.ShopGiayTheThao.repository.BrandRepository;

@Service
@Transactional(readOnly = true)
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public List<BrandDTO> getAllBrands() {
        return brandRepository.findAllByOrderByIdAsc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BrandDTO getBrandById(Long id) {
        return brandRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    private BrandDTO toDTO(Brand brand) {
        int count = brand.getProducts() != null ? brand.getProducts().size() : 0;
        String subtitle = getSubtitleByName(brand.getName());
        return new BrandDTO(
                brand.getId(),
                brand.getName(),
                brand.getLogo(),
                brand.getDescription(),
                subtitle,
                count
        );
    }

    private String getSubtitleByName(String name) {
        if (name == null) return "Biểu Tượng";
        String lower = name.toLowerCase();
        if (lower.contains("jordan")) return "Di sản vĩ đại";
        if (lower.contains("acg")) return "All Conditions Gear";
        if (lower.contains("sb")) return "Skateboarding";
        if (lower.contains("pro")) return "Hiệu Năng & Gym";
        if (lower.contains("converse")) return "Kinh Điển";
        return "Cốt Lõi";
    }
}
