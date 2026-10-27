package com.example.ShopGiayTheThao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import com.example.ShopGiayTheThao.dto.SizeRequest;
import com.example.ShopGiayTheThao.entity.Size;
import com.example.ShopGiayTheThao.service.SizeService;

@SpringBootTest
@Transactional
class SizeServiceTest {

    @Autowired
    private SizeService sizeService;

    @Test
    void testSizeCRUDPaginationAndSearch() {
        // 1. Thêm mới kích thước với ID tự sinh
        SizeRequest addReq = new SizeRequest("99-TEST");
        Size created = sizeService.createSize(addReq);

        assertNotNull(created.getId());
        assertEquals("99-TEST", created.getName());
        assertEquals("SIZ-" + created.getId(), created.getCode());

        // 2. Lấy chi tiết kích thước theo ID
        Size found = sizeService.getSizeById(created.getId());
        assertEquals("99-TEST", found.getName());
        assertEquals("SIZ-" + created.getId(), found.getCode());

        // 3. Cập nhật kích thước (chỉ cập nhật name)
        SizeRequest editReq = new SizeRequest(created.getId(), "99.5-TEST");
        Size updated = sizeService.updateSize(editReq);
        assertEquals("99.5-TEST", updated.getName());
        assertEquals("SIZ-" + updated.getId(), updated.getCode());

        // 4. Tìm kiếm theo tên kích thước và phân trang 5 phần tử / trang
        Page<Size> pageByName = sizeService.getSizesForAdmin(0, 5, "99.5");
        assertFalse(pageByName.isEmpty());
        assertEquals(5, pageByName.getSize());
        assertTrue(pageByName.getContent().stream().anyMatch(s -> s.getId().equals(created.getId())));

        // 5. Tìm kiếm theo định dạng mã "SIZ-ID"
        Page<Size> pageByCode = sizeService.getSizesForAdmin(0, 5, "SIZ-" + created.getId());
        assertFalse(pageByCode.isEmpty());
        assertTrue(pageByCode.getContent().stream().anyMatch(s -> s.getId().equals(created.getId())));

        // 6. Xóa kích thước
        sizeService.deleteSize(created.getId());

        // 7. Kiểm tra sắp xếp: Mới nhất xếp trước, cũ nhất xếp sau (ID giảm dần)
        Size sA = sizeService.createSize(new SizeRequest("ORDER-1"));
        Size sB = sizeService.createSize(new SizeRequest("ORDER-2"));
        Page<Size> pageRecent = sizeService.getSizesForAdmin(0, 5, "ORDER-");
        assertEquals(2, pageRecent.getContent().size());
        assertEquals(sB.getId(), pageRecent.getContent().get(0).getId());
        assertEquals(sA.getId(), pageRecent.getContent().get(1).getId());

        sizeService.deleteSize(sA.getId());
        sizeService.deleteSize(sB.getId());
    }
}
