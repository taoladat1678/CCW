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

import com.example.ShopGiayTheThao.dto.CustomerStatsResponse;
import com.example.ShopGiayTheThao.dto.UserRequest;
import com.example.ShopGiayTheThao.entity.User;
import com.example.ShopGiayTheThao.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Lấy tổng số lượng khách hàng.
     * Ưu tiên đếm theo role "CUSTOMER". Nếu không có bản ghi nào thì đếm toàn bộ users.
     */
    public long getTotalCustomers() {
        long customerCount = userRepository.countByRoleIgnoreCase("CUSTOMER");
        if (customerCount > 0) {
            return customerCount;
        }
        return userRepository.count();
    }

    public CustomerStatsResponse getCustomerStats() {
        long totalCustomers = getTotalCustomers();
        return new CustomerStatsResponse(totalCustomers, true);
    }
    /**
     * Lấy danh sách toàn bộ người dùng.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Lấy danh sách khách hàng.
     */
    public List<User> getAllCustomers() {
        List<User> customers = userRepository.findByRole("CUSTOMER");
        if (customers.isEmpty()) {
            return userRepository.findAll();
        }
        return customers;
    }

    /**
     * Tìm người dùng theo ID.
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy người dùng với ID: " + id));
    }

    /**
     * Tạo mới người dùng từ UserRequest.
     */
    public User createUser(UserRequest request) {
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email này đã được sử dụng!");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setRole(request.getRole() != null ? request.getRole() : "CUSTOMER");
        user.setStatus(request.getStatus() != null ? request.getStatus() : 1);

        return userRepository.save(user);
    }

    /**
     * Cập nhật thông tin người dùng.
     */
    public User updateUser(Long id, UserRequest request) {
        User existingUser = getUserById(id);

        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(existingUser.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email đã được sử dụng bởi tài khoản khác!");
            }
            existingUser.setEmail(request.getEmail());
        }

        if (request.getFullName() != null) {
            existingUser.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            existingUser.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            existingUser.setAddress(request.getAddress());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            existingUser.setPassword(request.getPassword());
        }
        if (request.getRole() != null) {
            existingUser.setRole(request.getRole());
        }
        if (request.getStatus() != null) {
            existingUser.setStatus(request.getStatus());
        }

        return userRepository.save(existingUser);
    }

    /**
     * Lấy danh sách người dùng phân trang, tìm kiếm và lọc vai trò/trạng thái cho Admin
     */
    @Transactional(readOnly = true)
    public Page<User> getUsersForAdmin(int page, int size, String keyword, String role, Integer status) {
        int pageNumber = Math.max(page, 0);
        int pageSize = (size > 0) ? size : 5;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        String kw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String cleanKw = kw;
        if (cleanKw != null) {
            String lower = cleanKw.toLowerCase();
            if (lower.startsWith("#usr-")) {
                cleanKw = cleanKw.substring(5).trim();
            } else if (lower.startsWith("usr-")) {
                cleanKw = cleanKw.substring(4).trim();
            } else if (lower.startsWith("#usr")) {
                cleanKw = cleanKw.substring(4).trim();
            } else if (lower.startsWith("usr")) {
                cleanKw = cleanKw.substring(3).trim();
            }
            String unpadded = cleanKw.replaceFirst("^0+", "");
            if (!unpadded.isEmpty()) {
                cleanKw = unpadded;
            }
        }

        String targetRole = null;
        if (role != null && !role.trim().isEmpty() && !"all".equalsIgnoreCase(role.trim())) {
            String r = role.trim();
            if ("member".equalsIgnoreCase(r) || "user".equalsIgnoreCase(r)) {
                targetRole = "CUSTOMER";
            } else {
                targetRole = r.toUpperCase();
            }
        }

        Integer targetStatus = null;
        if (status != null && (status == 0 || status == 1)) {
            targetStatus = status;
        }

        return userRepository.filterUsers(kw, cleanKw, targetRole, targetStatus, pageable);
    }

    /**
     * Cập nhật trạng thái người dùng (Chỉ sửa trạng thái theo yêu cầu: "sửa thì chỉ sửa ở trạng thái")
     */
    @Transactional
    public User updateUserStatus(Long id, Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trạng thái không hợp lệ (1: Hoạt động, 0: Tạm khóa)!");
        }
        User user = getUserById(id);
        user.setStatus(status);
        return userRepository.save(user);
    }

    /**
     * Khóa hoặc Mở khóa tài khoản người dùng nhanh (1-click toggle lock)
     */
    @Transactional
    public User toggleLockUser(Long id) {
        User user = getUserById(id);
        int currentStatus = (user.getStatus() != null) ? user.getStatus() : 1;
        int nextStatus = (currentStatus == 1) ? 0 : 1;
        user.setStatus(nextStatus);
        return userRepository.save(user);
    }

    /**
     * Xóa người dùng theo ID.
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        try {
            userRepository.delete(user);
            userRepository.flush();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Không thể xóa người dùng này do có dữ liệu liên kết (bài viết hoặc giỏ hàng/đơn hàng). Bạn có thể sử dụng chức năng Khóa tài khoản thay thế!");
        }
    }

    /**
     * Xác thực đăng nhập
     */
    public User login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng nhập đầy đủ email và mật khẩu!");
        }

        User user = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Email hoặc mật khẩu không chính xác!"));

        if (!password.equals(user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email hoặc mật khẩu không chính xác!");
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tài khoản của bạn đã bị khóa!");
        }

        return user;
    }

    /**
     * Kiểm tra email đã tồn tại hay chưa
     */
    public boolean existsByEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return userRepository.existsByEmail(email.trim());
    }
    public List<User> getRecentUsers() {
        return userRepository.findTop5ByOrderByIdDesc();
    }
}
