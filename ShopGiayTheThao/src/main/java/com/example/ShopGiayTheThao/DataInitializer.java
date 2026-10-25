// package com.example.ShopGiayTheThao;

// import java.math.BigDecimal;
// import java.util.Arrays;
// import java.util.List;

// import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;

// import com.example.ShopGiayTheThao.entity.Brand;
// import com.example.ShopGiayTheThao.entity.Category;
// import com.example.ShopGiayTheThao.entity.Product;
// import com.example.ShopGiayTheThao.entity.ProductSize;
// import com.example.ShopGiayTheThao.entity.Size;
// import com.example.ShopGiayTheThao.repository.BrandRepository;
// import com.example.ShopGiayTheThao.repository.CategoryRepository;
// import com.example.ShopGiayTheThao.repository.ProductRepository;
// import com.example.ShopGiayTheThao.repository.SizeRepository;

// /**
//  * Tự động tạo dữ liệu mẫu nếu CSDL ban đầu còn trống,
//  * giúp kiểm tra giao diện và API ngay lập tức mà không cần nhập thủ công.
//  */
// @Component
// public class DataInitializer implements CommandLineRunner {

//     private final CategoryRepository categoryRepository;
//     private final BrandRepository brandRepository;
//     private final SizeRepository sizeRepository;
//     private final ProductRepository productRepository;

//     public DataInitializer(
//             CategoryRepository categoryRepository,
//             BrandRepository brandRepository,
//             SizeRepository sizeRepository,
//             ProductRepository productRepository) {
//         this.categoryRepository = categoryRepository;
//         this.brandRepository = brandRepository;
//         this.sizeRepository = sizeRepository;
//         this.productRepository = productRepository;
//     }

//     @Override
//     public void run(String... args) throws Exception {
//         if (categoryRepository.count() == 0) {
//             Category c1 = categoryRepository.save(new Category("Giày Lifestyle / Đời Sống", "Phong cách đường phố và đời sống hàng ngày", 1));
//             Category c2 = categoryRepository.save(new Category("Giày Chạy Bộ (Running)", "Tối ưu hiệu suất và êm ái trên từng bước chạy", 1));
//             Category c3 = categoryRepository.save(new Category("Giày Bóng Rổ (Basketball)", "Đệm khí đàn hồi và bám sân xuất sắc", 1));
//             Category c4 = categoryRepository.save(new Category("Giày Dã Ngoại & Leo Núi", "Chống chịu thời tiết và địa hình khắc nghiệt", 1));

//             Brand b1 = brandRepository.save(new Brand("Nike Sportswear", null, "Dòng thời trang thể thao huyền thoại"));
//             Brand b2 = brandRepository.save(new Brand("Jordan Brand", null, "Biểu tượng bóng rổ và phong cách"));
//             Brand b3 = brandRepository.save(new Brand("Nike SB", null, "Dành riêng cho giới trượt ván"));
//             Brand b4 = brandRepository.save(new Brand("Nike ACG", null, "All Conditions Gear cho dân dã ngoại"));

//             Size s1 = sizeRepository.save(new Size("38.5"));
//             Size s2 = sizeRepository.save(new Size("39"));
//             Size s3 = sizeRepository.save(new Size("40"));
//             Size s4 = sizeRepository.save(new Size("40.5"));
//             Size s5 = sizeRepository.save(new Size("41"));
//             Size s6 = sizeRepository.save(new Size("42"));
//             Size s7 = sizeRepository.save(new Size("42.5"));
//             Size s8 = sizeRepository.save(new Size("43"));
//             Size s9 = sizeRepository.save(new Size("44"));

//             List<Size> allSizes = Arrays.asList(s1, s2, s3, s4, s5, s6, s7, s8, s9);

//             createProduct("Nike Air Max Pulse Roam", "Giày thể thao nam cao cấp với đệm Air Max êm ái", new BigDecimal("4499000"), null, "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=700&h=700&q=80", c1, b1, allSizes);
//             createProduct("Nike Dunk Low Retro", "Đôi giày huyền thoại phong cách retro cổ điển", new BigDecimal("3599000"), new BigDecimal("2699000"), "https://images.unsplash.com/photo-1600185365483-26d7a4cc7519?auto=format&fit=crop&w=700&h=700&q=80", c1, b1, allSizes);
//             createProduct("Nike Pegasus 41 GORE-TEX", "Giày chạy bộ chuyên nghiệp chống thấm nước tuyệt đối", new BigDecimal("3829000"), null, "https://images.unsplash.com/photo-1608231387042-66d1773070a5?auto=format&fit=crop&w=700&h=700&q=80", c2, b1, allSizes);
//             createProduct("Air Jordan 1 Low OG", "Thiết kế kinh điển với logo Wings và phối màu iconic", new BigDecimal("4109000"), null, "https://images.unsplash.com/photo-1516478177764-9fe5bd7e9717?auto=format&fit=crop&w=700&h=700&q=80", c3, b2, allSizes);
//             createProduct("Nike ACG Mountain Fly 2 Low", "Khả năng bám đường tối ưu trên mọi địa hình hiểm trở", new BigDecimal("4529000"), null, "https://images.unsplash.com/photo-1515955656352-a1fa3ffcd111?auto=format&fit=crop&w=700&h=700&q=80", c4, b4, allSizes);
//             createProduct("Nike Air Force 1 '07 LV8", "Biểu tượng thời trang đường phố vượt thời gian", new BigDecimal("3499000"), new BigDecimal("2449000"), "https://images.unsplash.com/photo-1579338559194-a162d19bf842?auto=format&fit=crop&w=700&h=700&q=80", c1, b1, allSizes);
//             createProduct("Nike Invincible 3", "Đệm bọt ZoomX siêu dày cho cảm giác chạy nhẹ như bay", new BigDecimal("5129000"), new BigDecimal("4299000"), "https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?auto=format&fit=crop&w=700&h=700&q=80", c2, b1, allSizes);
//             createProduct("Nike SB Dunk Low Pro", "Thiết kế lưỡi gà dày và đệm gót bảo vệ cổ chân", new BigDecimal("3299000"), null, "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?auto=format&fit=crop&w=700&h=700&q=80", c1, b3, allSizes);
//             createProduct("Air Jordan 4 Retro", "Mẫu giày bóng rổ đỉnh cao phong cách thập niên 90", new BigDecimal("5999000"), new BigDecimal("4999000"), "https://images.unsplash.com/photo-1552346154-21d32810aba3?auto=format&fit=crop&w=700&h=700&q=80", c3, b2, allSizes);
//             createProduct("Nike Air Zoom Pegasus 40", "Mẫu giày chạy bộ bán chạy hàng đầu với độ nảy cao", new BigDecimal("3690000"), new BigDecimal("2990000"), "https://images.unsplash.com/photo-1514989940723-e8e51635b782?auto=format&fit=crop&w=700&h=700&q=80", c2, b1, allSizes);
//             createProduct("Nike Blazer Mid '77 Vintage", "Giày cổ cao phong cách bóng rổ thập niên 70", new BigDecimal("2890000"), null, "https://images.unsplash.com/photo-1597045566677-8cf032ed6634?auto=format&fit=crop&w=700&h=700&q=80", c1, b1, allSizes);
//             createProduct("Nike Metcon 9", "Giày luyện tập thể hình và cử tạ chuyên dụng", new BigDecimal("4190000"), null, "https://images.unsplash.com/photo-1582588678413-dbf45f4823e9?auto=format&fit=crop&w=700&h=700&q=80", c1, b1, allSizes);
//         }
//     }

//     private void createProduct(String name, String desc, BigDecimal price, BigDecimal salePrice, String thumb, Category cat, Brand brand, List<Size> sizes) {
//         Product p = new Product(name, desc, price, salePrice, thumb, cat, brand, 1);
//         for (Size s : sizes) {
//             p.getProductSizes().add(new ProductSize(p, s, 20));
//         }
//         productRepository.save(p);
//     }
// }
