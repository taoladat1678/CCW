package com.example.ShopGiayTheThao.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.ShopGiayTheThao.dto.SizeRequest;
import com.example.ShopGiayTheThao.entity.Size;
import com.example.ShopGiayTheThao.repository.SizeRepository;

@Service
public class SizeService {

    private final SizeRepository sizeRepository;

    public SizeService(SizeRepository sizeRepository) {
        this.sizeRepository = sizeRepository;
    }

    /**
     * Lấy danh sách kích thước phân trang (mặc định 5 size / trang) và tìm kiếm cho Admin
     */
    @Transactional(readOnly = true)
    public Page<Size> getSizesForAdmin(int page, int size, String keyword) {
        int pageNumber = Math.max(page, 0);
        int pageSize = (size > 0) ? size : 5;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        String kw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String cleanKw = kw;
        if (cleanKw != null) {
            String lower = cleanKw.toLowerCase();
            if (lower.startsWith("siz-")) {
                cleanKw = cleanKw.substring(4).trim();
            } else if (lower.startsWith("siz")) {
                cleanKw = cleanKw.substring(3).trim();
            }
            // Bỏ các số 0 ở đầu nếu người dùng tìm kiếm dạng SIZ-001 hoặc 001
            String unpadded = cleanKw.replaceFirst("^0+", "");
            if (!unpadded.isEmpty()) {
                cleanKw = unpadded;
            }
        }

        return sizeRepository.filterSizes(kw, cleanKw, pageable);
    }

    public Page<Size> getSizesForAdmin(int page, int size) {
        return getSizesForAdmin(page, size, null);
    }

    /**
     * Lấy chi tiết kích thước theo ID
     */
    @Transactional(readOnly = true)
    public Size getSizeById(Long id) {
        return sizeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy kích thước với ID: " + id));
    }

    /**
     * Thêm mới kích thước
     * Chỉ lưu trường name và MySQL tự sinh ID số tăng dần.
     * Không lưu các trường hệ đo lường hay trạng thái.
     */
    @Transactional
    public Size createSize(SizeRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên kích thước không được để trống!");
        }

        String trimmedName = request.getName().trim();
        if (sizeRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kích thước \"" + trimmedName + "\" đã tồn tại trong hệ thống!");
        }

        Size size = new Size();
        size.setName(trimmedName);

        // Lưu vào database với ID tự sinh
        return sizeRepository.save(size);
    }

    /**
     * Cập nhật kích thước
     * Chỉ cập nhật trường name.
     */
    @Transactional
    public Size updateSize(SizeRequest request) {
        if (request.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID kích thước không được để trống!");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên kích thước không được để trống!");
        }

        String trimmedName = request.getName().trim();
        if (sizeRepository.existsByNameIgnoreCaseAndIdNot(trimmedName, request.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên kích thước \"" + trimmedName + "\" đã bị trùng với kích thước khác!");
        }

        Size size = getSizeById(request.getId());
        size.setName(trimmedName);

        return sizeRepository.save(size);
    }

    /**
     * Xóa kích thước (xử lý an toàn ràng buộc khóa ngoại)
     */
    @Transactional
    public void deleteSize(Long id) {
        Size size = getSizeById(id);
        if (size.getProductSizes() != null && !size.getProductSizes().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Kích thước này đang được liên kết với " + size.getProductSizes().size() + " sản phẩm trong hệ thống, không thể xóa!");
        }
        sizeRepository.deleteById(id);
    }

    /**
     * Lấy tổng số lượng kích thước
     */
    public long getTotalSizes() {
        return sizeRepository.count();
    }

    /**
     * Lấy toàn bộ kích thước sắp xếp theo ID giảm dần (mới nhất xếp trước, cũ nhất xếp sau)
     */
    public List<Size> getAllSizes() {
        return sizeRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }
}
