package com.example.ShopGiayTheThao.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.ShopGiayTheThao.dto.BrandDTO;
import com.example.ShopGiayTheThao.dto.BrandRequest;
import com.example.ShopGiayTheThao.entity.Brand;
import com.example.ShopGiayTheThao.entity.Product;
import com.example.ShopGiayTheThao.repository.BrandRepository;
import com.example.ShopGiayTheThao.repository.ProductRepository;

@Service
@Transactional(readOnly = true)
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    public BrandServiceImpl(BrandRepository brandRepository, ProductRepository productRepository) {
        this.brandRepository = brandRepository;
        this.productRepository = productRepository;
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

    @Override
    public Page<BrandDTO> getBrandsForAdmin(int page, int size, String keyword) {
        int pageNumber = Math.max(page, 0);
        int pageSize = (size > 0) ? size : 4;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        String kw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String cleanKw = kw;
        if (cleanKw != null) {
            String lower = cleanKw.toLowerCase();
            if (lower.startsWith("brd-")) {
                cleanKw = cleanKw.substring(4).trim();
            } else if (lower.startsWith("brd")) {
                cleanKw = cleanKw.substring(3).trim();
            }
            cleanKw = cleanKw.replaceFirst("^0+(?!$)", "");
        }

        return brandRepository.filterBrands(kw, cleanKw, pageable).map(this::toDTO);
    }

    @Override
    public Page<BrandDTO> getBrandsForAdmin(int page, int size) {
        return getBrandsForAdmin(page, size, null);
    }

    @Override
    public Brand getBrandEntityById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu với ID: " + id));
    }

    @Override
    @Transactional
    public Brand createBrand(BrandRequest request) {
        if (request == null || request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên thương hiệu không được để trống!");
        }

        String trimmedName = request.getName().trim();
        if (brandRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên thương hiệu đã tồn tại trong hệ thống!");
        }

        Brand brand = new Brand();
        brand.setName(trimmedName);
        brand.setLogo((request.getLogo() != null && !request.getLogo().isBlank()) ? request.getLogo().trim() : null);
        brand.setDescription((request.getDescription() != null && !request.getDescription().isBlank()) ? request.getDescription().trim() : null);

        return brandRepository.save(brand);
    }

    @Override
    @Transactional
    public Brand updateBrand(BrandRequest request) {
        if (request == null || request.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID thương hiệu không được để trống!");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên thương hiệu không được để trống!");
        }

        Brand brand = getBrandEntityById(request.getId());
        String trimmedName = request.getName().trim();

        if (brandRepository.existsByNameIgnoreCaseAndIdNot(trimmedName, request.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên thương hiệu đã được sử dụng bởi thương hiệu khác!");
        }

        brand.setName(trimmedName);

        // Nếu người dùng nhập link ảnh mới thì cập nhật; nếu để trống thì giữ lại ảnh cũ
        if (request.getLogo() != null) {
            String logoTrim = request.getLogo().trim();
            if (!logoTrim.isEmpty()) {
                brand.setLogo(logoTrim);
            }
        }
        if (request.getDescription() != null) {
            brand.setDescription(!request.getDescription().isBlank() ? request.getDescription().trim() : null);
        }

        return brandRepository.save(brand);
    }

    @Override
    @Transactional
    public void deleteBrand(Long id) {
        Brand brand = getBrandEntityById(id);

        List<Product> products = productRepository.findAllByBrandId(id);
        if (!products.isEmpty()) {
            List<Brand> otherBrands = brandRepository.findAll().stream()
                    .filter(b -> !b.getId().equals(id))
                    .collect(Collectors.toList());

            Brand targetBrand;
            if (!otherBrands.isEmpty()) {
                targetBrand = otherBrands.get(ThreadLocalRandom.current().nextInt(otherBrands.size()));
            } else {
                targetBrand = brandRepository.save(new Brand("Khác", null, "Thương hiệu mặc định hệ thống"));
            }

            for (Product p : products) {
                p.setBrand(targetBrand);
            }
            productRepository.saveAll(products);
            productRepository.flush();
        }

        if (brand.getProducts() != null) {
            brand.getProducts().clear();
        }
        brandRepository.delete(brand);
        brandRepository.flush();
    }

    @Override
    public long getTotalBrands() {
        return brandRepository.count();
    }

    private BrandDTO toDTO(Brand brand) {
        int count = brand.getProducts() != null ? brand.getProducts().size() : 0;
        return new BrandDTO(
                brand.getId(),
                brand.getName(),
                brand.getLogo(),
                brand.getDescription(),
                null,
                count
        );
    }
}
