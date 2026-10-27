package com.example.ShopGiayTheThao.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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

    public long getTotalCustomers() {
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
     * Xóa người dùng theo ID.
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng để xóa với ID: " + id);
        }
        userRepository.deleteById(id);
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
