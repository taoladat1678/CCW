-- ==============================================================================
-- T1SHOESPORT - DỮ LIỆU MẪU ĐẦY ĐỦ (MYSQL / PHPMYADMIN / NAVICAT / WORKBENCH)
-- ==============================================================================
-- Hướng dẫn:
-- 1. Mở MySQL Workbench, phpMyAdmin, Navicat hoặc DBeaver.
-- 2. Kết nối tới MySQL Server (root@localhost:3306).
-- 3. Mở file này và chạy toàn bộ (Run Script).
-- ==============================================================================

CREATE DATABASE IF NOT EXISTS `shopgiaythethao` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `shopgiaythethao`;

SET FOREIGN_KEY_CHECKS = 0;

-- 1. Xóa các bảng cũ nếu tồn tại
DROP TABLE IF EXISTS `cart_items`;
DROP TABLE IF EXISTS `product_sizes`;
DROP TABLE IF EXISTS `product_images`;
DROP TABLE IF EXISTS `blogs`;
DROP TABLE IF EXISTS `products`;
DROP TABLE IF EXISTS `sizes`;
DROP TABLE IF EXISTS `categories`;
DROP TABLE IF EXISTS `brands`;
DROP TABLE IF EXISTS `users`;

-- 2. Tạo cấu trúc các bảng
CREATE TABLE `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `full_name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `phone` VARCHAR(20) DEFAULT NULL,
    `address` VARCHAR(255) DEFAULT NULL,
    `role` VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    `status` INT NOT NULL DEFAULT 1,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `brands` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `logo` VARCHAR(255) DEFAULT NULL,
    `description` TEXT DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `categories` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `description` TEXT DEFAULT NULL,
    `status` INT NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `sizes` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `products` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(200) NOT NULL,
    `description` TEXT DEFAULT NULL,
    `price` DECIMAL(12,2) NOT NULL,
    `sale_price` DECIMAL(12,2) DEFAULT NULL,
    `thumbnail` VARCHAR(255) DEFAULT NULL,
    `category_id` BIGINT NOT NULL,
    `brand_id` BIGINT NOT NULL,
    `status` INT NOT NULL DEFAULT 1,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_products_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`),
    CONSTRAINT `fk_products_brand` FOREIGN KEY (`brand_id`) REFERENCES `brands` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `product_images` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `product_id` BIGINT NOT NULL,
    `url` VARCHAR(255) NOT NULL,
    CONSTRAINT `fk_images_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `product_sizes` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `product_id` BIGINT NOT NULL,
    `size_id` BIGINT NOT NULL,
    `stock` INT NOT NULL DEFAULT 0,
    CONSTRAINT `fk_ps_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_ps_size` FOREIGN KEY (`size_id`) REFERENCES `sizes` (`id`),
    CONSTRAINT `product_sizes_unique_0` UNIQUE (`product_id`, `size_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `blogs` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(255) NOT NULL,
    `content` TEXT DEFAULT NULL,
    `thumbnail` VARCHAR(255) DEFAULT NULL,
    `author_id` BIGINT NOT NULL,
    `status` INT NOT NULL DEFAULT 1,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_blogs_author` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `cart_items` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `size_id` BIGINT NOT NULL,
    `quantity` INT NOT NULL DEFAULT 1,
    CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    CONSTRAINT `fk_cart_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
    CONSTRAINT `fk_cart_size` FOREIGN KEY (`size_id`) REFERENCES `sizes` (`id`),
    CONSTRAINT `cart_items_unique_0` UNIQUE (`user_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- 3. INSERT DỮ LIỆU MẪU (USERS)
-- ==============================================================================
INSERT INTO `users` (`id`, `full_name`, `email`, `password`, `phone`, `address`, `role`, `status`, `created_at`) VALUES
(1, 'Quản Trị Viên Hệ Thống', 'admin@t1shoesport.vn', '123456', '0901234567', 'T1 Flagship Store, Q.1, TP. Hồ Chí Minh', 'ADMIN', 1, '2026-01-10 08:30:00'),
(2, 'Nguyễn Hoàng Nam', 'hoangnam@t1shoesport.vn', '123456', '0909876543', 'Quận Hoàn Kiếm, Hà Nội', 'ADMIN', 1, '2026-01-15 09:00:00'),
(3, 'Trần Minh Thư (Stylist)', 'minhthu@t1shoesport.vn', '123456', '0912345678', 'Quận Hải Châu, Đà Nẵng', 'ADMIN', 1, '2026-02-01 10:15:00'),
(4, 'Lê Minh Hùng', 'hung.le@gmail.com', '123456', '0988776655', 'Số 18 Cầu Giấy, Hà Nội', 'CUSTOMER', 1, '2026-02-12 14:20:00'),
(5, 'Phạm Thu Trang', 'thutrang.pham@gmail.com', '123456', '0977665544', 'Chung cư Landmark 81, Bình Thạnh, TP.HCM', 'CUSTOMER', 1, '2026-02-20 16:45:00'),
(6, 'Đỗ Anh Quân', 'quan.do@gmail.com', '123456', '0933221100', 'Phường Bến Nghé, Quận 1, TP.HCM', 'CUSTOMER', 1, '2026-03-01 11:10:00'),
(7, 'Vũ Hoàng Long', 'long.vu@outlook.com', '123456', '0944556677', 'Khu Đô Thị Ecopark, Hưng Yên', 'CUSTOMER', 1, '2026-03-05 15:30:00'),
(8, 'Nguyễn Mai Anh', 'maianh.nguyen@yahoo.com', '123456', '0966778899', 'Đường 30/4, Ninh Kiều, Cần Thơ', 'CUSTOMER', 1, '2026-03-12 08:50:00'),
(9, 'Bùi Quốc Bảo', 'bao.bui@gmail.com', '123456', '0922334455', 'Bãi Cháy, TP. Hạ Long, Quảng Ninh', 'CUSTOMER', 1, '2026-03-18 19:25:00'),
(10, 'Đặng Thu Hà', 'thuha.dang@gmail.com', '123456', '0911223344', 'Phường Tân Phong, Biên Hòa, Đồng Nai', 'CUSTOMER', 1, '2026-04-02 13:40:00'),
(11, 'Trần Văn Kiên (Tạm Khóa)', 'kien.tran@gmail.com', '123456', '0903344556', 'TP. Vũng Tàu, Bà Rịa - Vũng Tàu', 'CUSTOMER', 0, '2026-04-10 10:00:00'),
(12, 'Hoàng Thùy Linh (Tạm Khóa)', 'linh.hoang@gmail.com', '123456', '0988112233', 'Đường Trần Phú, TP. Nha Trang, Khánh Hòa', 'CUSTOMER', 0, '2026-04-15 17:15:00');

-- ==============================================================================
-- 4. INSERT DỮ LIỆU MẪU (BRANDS)
-- ==============================================================================
INSERT INTO `brands` (`id`, `name`, `logo`, `description`) VALUES
(1, 'Nike', 'https://upload.wikimedia.org/wikipedia/commons/a/a6/Logo_NIKE.svg', 'Thương hiệu thể thao số 1 thế giới với tinh thần Just Do It và công nghệ đệm khí tiên phong Air, ZoomX, Dynamic Air.'),
(2, 'Jordan Brand', 'https://upload.wikimedia.org/wikipedia/en/3/37/Jumpman_logo.svg', 'Di sản bóng rổ bất tử của huyền thoại Michael Jordan kết hợp phong cách văn hóa đường phố Streetwear cao cấp.'),
(3, 'Adidas', 'https://upload.wikimedia.org/wikipedia/commons/2/20/Adidas_Logo.svg', 'Thương hiệu 3 sọc trứ danh nước Đức với những công nghệ đệm đỉnh cao Ultraboost, Lightstrike Pro và phong cách Originals.'),
(4, 'Puma', 'https://upload.wikimedia.org/wikipedia/en/thumb/4/44/Puma_logo.svg/320px-Puma_logo.svg.png', 'Thương hiệu báo đốm tốc độ, tiên phong với bộ đệm Nitro Foam siêu nhẹ trong điền kinh và bóng rổ thế giới.'),
(5, 'New Balance', 'https://upload.wikimedia.org/wikipedia/commons/e/ea/New_Balance_logo.svg', 'Đỉnh cao giày chạy thủ công Mỹ với thiết kế Dad Shoes cổ điển, bền bỉ và chất lượng da lộn thượng hạng.'),
(6, 'Asics', 'https://upload.wikimedia.org/wikipedia/commons/b/b1/Asics_Logo.svg', 'Biểu tượng công nghệ đệm Gel và FlyteFoam của Nhật Bản, bảo vệ tối đa khớp gối và cổ chân vận động viên.'),
(7, 'Converse', 'https://upload.wikimedia.org/wikipedia/commons/3/30/Converse_logo.svg', 'Biểu tượng văn hóa đại chúng hơn một thế kỷ với dòng giày vải kinh điển Chuck Taylor All Star.'),
(8, 'Vans', 'https://upload.wikimedia.org/wikipedia/commons/a/aa/Vans-logo.png', 'Hơi thở ván trượt California với đế bánh quế Waffle và phong cách Off The Wall phóng khoáng.');

-- ==============================================================================
-- 5. INSERT DỮ LIỆU MẪU (CATEGORIES)
-- ==============================================================================
INSERT INTO `categories` (`id`, `name`, `description`, `status`) VALUES
(1, 'Giày Chạy Bộ (Running)', 'Dòng giày tối ưu hóa tốc độ, độ nảy và độ êm ái cho cự ly 5K, 10K, Bán Marathon và Full Marathon 42KM.', 1),
(2, 'Giày Bóng Rổ (Basketball)', 'Giày thi đấu có độ bám sân cao, hỗ trợ bật nhảy và bảo vệ mắt cá chân toàn diện trong các pha xoay người.', 1),
(3, 'Giày Sneaker & Streetwear', 'Phong cách thời trang đường phố đương đại, dễ dàng phối hợp trang phục hàng ngày đi làm, đi chơi.', 1),
(4, 'Giày Tập Luyện & Gym (Training)', 'Đế phẳng vững chắc, chịu tải tốt cho các bài nâng tạ squats, deadlifts và cardio cường độ cao HIIT.', 1),
(5, 'Giày Dã Ngoại & Trail (Outdoor ACG)', 'Chất liệu chống thấm nước Gore-Tex, đế gai bám bùn đất phục vụ trekking, leo núi và chạy địa hình.', 1),
(6, 'Dép & Sandal Thể Thao (Slides)', 'Dép đúc nguyên khối siêu êm giúp thư giãn và phục hồi bàn chân nhanh chóng sau giờ tập luyện.', 1);

-- ==============================================================================
-- 6. INSERT DỮ LIỆU MẪU (SIZES)
-- ==============================================================================
INSERT INTO `sizes` (`id`, `name`) VALUES
(1, '36'),
(2, '36.5'),
(3, '37'),
(4, '37.5'),
(5, '38'),
(6, '38.5'),
(7, '39'),
(8, '39.5'),
(9, '40'),
(10, '40.5'),
(11, '41'),
(12, '41.5'),
(13, '42'),
(14, '42.5'),
(15, '43'),
(16, '43.5'),
(17, '44'),
(18, '44.5'),
(19, '45');

-- ==============================================================================
-- 7. INSERT DỮ LIỆU MẪU (PRODUCTS - 25 ĐÔI GIÀY HOT NHẤT)
-- ==============================================================================
INSERT INTO `products` (`id`, `name`, `description`, `price`, `sale_price`, `thumbnail`, `category_id`, `brand_id`, `status`, `created_at`) VALUES
(1, 'Nike Air Max Dn "All Night" Black Purple', 'Thiết kế tương lai với công nghệ 4 ống Dynamic Air hai buồng áp suất kép mang lại cảm giác nảy êm chưa từng có.', 3990000.00, 3490000.00, 'https://images.unsplash.com/photo-1556906781-9a412961c28c?auto=format&fit=crop&w=700&q=80', 3, 1, 1, '2026-03-01 10:00:00'),
(2, 'Nike Air Zoom Pegasus 41 White Pure Platinum', 'Đôi giày chạy bộ quốc dân thế hệ thứ 41 trang bị đệm bọt ReactX mới tăng 13% độ hoàn trả năng lượng.', 3490000.00, 2990000.00, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=700&q=80', 1, 1, 1, '2026-03-02 11:30:00'),
(3, 'Air Jordan 1 Retro High OG "Chicago Lost & Found"', 'Biểu tượng huyền thoại Michael Jordan trở lại với chất liệu da nứt cổ điển vintage và hộp giày nguyên bản thập niên 85.', 5490000.00, 4890000.00, 'https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?auto=format&fit=crop&w=700&q=80', 3, 2, 1, '2026-03-03 14:15:00'),
(4, 'Nike Dunk Low Retro "Panda" Black White', 'Đôi giày sneaker phối màu đen trắng bất hủ, dễ dàng phối đồ với mọi phong cách từ thanh lịch đến dạo phố.', 2990000.00, 2490000.00, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?auto=format&fit=crop&w=700&q=80', 3, 1, 1, '2026-03-04 09:20:00'),
(5, 'Adidas Ultraboost Light Core Black Solar Red', 'Thế hệ đệm Boost nhẹ hơn 30% giúp từng bước chạy nhẹ tựa lông hồng, ôm sát bàn chân với thân vải dệt Primeknit+.', 4500000.00, 3850000.00, 'https://images.unsplash.com/photo-1587563871167-1ee9c731aefb?auto=format&fit=crop&w=700&q=80', 1, 3, 1, '2026-03-05 16:40:00'),
(6, 'Adidas Samba OG Cloud White Core Black', 'Cơn sốt thời trang toàn cầu với dáng giày bóng đá trong nhà cổ điển thập niên 50, mũi da lộn chữ T sang trọng.', 2790000.00, 2390000.00, 'https://images.unsplash.com/photo-1518002171953-a080ee817e1f?auto=format&fit=crop&w=700&q=80', 3, 3, 1, '2026-03-06 13:00:00'),
(7, 'Puma Deviate Nitro 2 Carbon Running Shoe', 'Siêu giày chạy bộ tích hợp đĩa đệm sợi carbon INNOPLATE và lớp đệm Nitro Elite dành cho các vận động viên săn PR.', 3800000.00, 3190000.00, 'https://images.unsplash.com/photo-1608231387042-66d1773070a5?auto=format&fit=crop&w=700&q=80', 1, 4, 1, '2026-03-07 10:45:00'),
(8, 'New Balance 550 White Green Vintage Court', 'Cú lội ngược dòng ngoạn mục của dòng giày bóng rổ cổ thấp thập niên 80 với chất liệu da cao cấp và phối màu xanh retro.', 2950000.00, 2550000.00, 'https://images.unsplash.com/photo-1539185441755-769473a23570?auto=format&fit=crop&w=700&q=80', 3, 5, 1, '2026-03-08 15:10:00'),
(9, 'New Balance 990v6 Made in USA Castlerock Grey', 'Đỉnh cao di sản thủ công Boston kết hợp giữa đệm bọt FuelCell hiện đại và chất liệu da lộn lợn rừng mềm mại.', 5890000.00, 5200000.00, 'https://images.unsplash.com/photo-1607522370275-f14206abe5d3?auto=format&fit=crop&w=700&q=80', 3, 5, 1, '2026-03-09 11:25:00'),
(10, 'Asics Gel-Kayano 30 Anniversary Edition', 'Hệ thống nâng đỡ 4D GUIDANCE SYSTEM cách mạng hóa độ vững chãi cho người chân bè, chống lật sơ mi tối đa.', 3990000.00, 3590000.00, 'https://images.unsplash.com/photo-1579338559194-a162d19bf842?auto=format&fit=crop&w=700&q=80', 1, 6, 1, '2026-03-10 17:00:00'),
(11, 'Nike Vaporfly 3 Next% Ekiden Marathon Racer', 'Cỗ máy phá kỷ lục marathon thế giới với bộ đệm ZoomX toàn phần dày dặn và tấm carbon nguyên khối Flyplate.', 6290000.00, 5690000.00, 'https://images.unsplash.com/photo-1515955656352-a1fa3ffcd111?auto=format&fit=crop&w=700&q=80', 1, 1, 1, '2026-03-11 08:30:00'),
(12, 'Air Jordan 4 Retro "Military Black" Neutral Grey', 'Kiệt tác thiết kế của Tinker Hatfield với lưới thoát khí bên hông, cánh xỏ dây nhựa đặc trưng và đệm Air êm ái.', 6500000.00, 5900000.00, 'https://images.unsplash.com/photo-1552346154-21d32810aba3?auto=format&fit=crop&w=700&q=80', 2, 2, 1, '2026-03-12 14:00:00'),
(13, 'Nike Air Force 1 07 All White Triple Classic', 'Đôi giày huyền thoại không bao giờ lỗi thời từ năm 1982, da trơn cao cấp dễ lau chùi và đế khâu viền cực bền.', 2690000.00, 2390000.00, 'https://images.unsplash.com/photo-1597045566677-8cf032ed6634?auto=format&fit=crop&w=700&q=80', 3, 1, 1, '2026-03-13 12:15:00'),
(14, 'Adidas Adizero Adios Pro 3 Solar Orange', 'Đôi giày thống trị các bục huy chương World Marathon Majors trang bị thanh đệm carbon EnergyRods mô phỏng xương chân.', 5500000.00, 4950000.00, 'https://images.unsplash.com/photo-1560769629-975ec94e6a86?auto=format&fit=crop&w=700&q=80', 1, 3, 1, '2026-03-14 16:20:00'),
(15, 'Nike Metcon 9 Training Gym & Crossfit', 'Bàn đạp vững chãi với tấm đệm gót Hyperlift mở rộng hỗ trợ nâng tạ nặng, viền hông bọc cao su leo dây bền bỉ.', 3290000.00, 2890000.00, 'https://images.unsplash.com/photo-1575537302964-96cd47c06b1b?auto=format&fit=crop&w=700&q=80', 4, 1, 1, '2026-03-15 09:40:00'),
(16, 'Nike ACG Mountain Fly 2 Low Gore-Tex Trail', 'Đế gai bám lấy cảm hứng từ lốp xe đạp địa hình, màng chống thấm nước GORE-TEX bảo vệ bàn chân trong mọi cơn mưa.', 4290000.00, 3790000.00, 'https://images.unsplash.com/photo-1514989940723-e8e51635b782?auto=format&fit=crop&w=700&q=80', 5, 1, 1, '2026-03-16 11:50:00'),
(17, 'Puma MB.03 LaMelo Ball "Toxic" Basketball', 'Phong cách quái dị phá cách của siêu sao LaMelo Ball với họa tiết cào xé hoang dã và bộ đệm Nitro Foam siêu nhạy.', 3590000.00, 3190000.00, 'https://images.unsplash.com/photo-1549298916-b41d501d3772?auto=format&fit=crop&w=700&q=80', 2, 4, 1, '2026-03-17 15:35:00'),
(18, 'Converse Chuck 70 Vintage Canvas High Top Black', 'Phiên bản Chuck 70 vải bạt dày dặn 12oz, đệm lót OrthoLite êm ái và viền đế cao su màu trắng ngà đánh bóng cổ điển.', 2100000.00, 1850000.00, 'https://images.unsplash.com/photo-1607522370275-f14206abe5d3?auto=format&fit=crop&w=700&q=80', 3, 7, 1, '2026-03-18 10:10:00'),
(19, 'Vans Knu Skool Black White Chunky Puffy', 'Sự trở lại của thiết kế Fat Tongue thập niên 90 với lưỡi gà dày cộm, sọc Sidestripe 3D dập nổi đậm chất trượt ván.', 2250000.00, 1990000.00, 'https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?auto=format&fit=crop&w=700&q=80', 3, 8, 1, '2026-03-19 13:20:00'),
(20, 'Asics Novablast 4 Đệm Bật Năng Động Bứt Phá', 'Hình học đế đệm phong cách origami và bọt FF BLAST PLUS ECO mang lại trải nghiệm chạy nảy tưng bừng phấn khích.', 3490000.00, 3090000.00, 'https://images.unsplash.com/photo-1460353581641-37baddab0fa2?auto=format&fit=crop&w=700&q=80', 1, 6, 1, '2026-03-20 08:45:00'),
(21, 'Nike Calm Slide Dép Đúc Siêu Nhẹ Kháng Nước', 'Thiết kế tối giản nguyên khối từ bọt EVA mềm dẻo, kháng nước 100% cực kỳ thích hợp đi biển và thư giãn sau khi tập.', 1390000.00, 1190000.00, 'https://images.unsplash.com/photo-1603808033192-082d6919d3e1?auto=format&fit=crop&w=700&q=80', 6, 1, 1, '2026-03-21 14:15:00'),
(22, 'Adidas Adilette 22 Slides Futuristic Desert', 'Thiết kế địa hình 3D lấy cảm hứng từ chuyến thám hiểm sao Hỏa, chất liệu bọt sinh học thân thiện với môi trường.', 1490000.00, 1250000.00, 'https://images.unsplash.com/photo-1579338559194-a162d19bf842?auto=format&fit=crop&w=700&q=80', 6, 3, 1, '2026-03-22 17:30:00'),
(23, 'Jordan Tatum 2 "Vortex" Lightest Basketball', 'Mẫu giày thi đấu nhẹ nhất dòng sản phẩm bóng rổ Jordan của Jayson Tatum, giảm tải áp lực tối đa cho đôi chân.', 3490000.00, 2990000.00, 'https://images.unsplash.com/photo-1512374382149-233c42b6a83b?auto=format&fit=crop&w=700&q=80', 2, 2, 1, '2026-03-23 11:05:00'),
(24, 'Nike Invincible 3 Max Cushion ZoomX Running', 'Độ đệm tối đa bảo vệ đôi chân bạn khỏi va đập chấn thương, thân giày Flyknit thoáng khí với đai giữ gót chân chắc chắn.', 4790000.00, 4190000.00, 'https://images.unsplash.com/photo-1562183241-b937e95585b6?auto=format&fit=crop&w=700&q=80', 1, 1, 1, '2026-03-24 15:40:00'),
(25, 'Nike Zoom Vomero 5 Supersonic Cyber Silver', 'Sự hồi sinh rực rỡ của phong cách Y2K Runner với các chi tiết phản quang sắc nét và hệ thống thông gió thông minh.', 3890000.00, 3390000.00, 'https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?auto=format&fit=crop&w=700&q=80', 3, 1, 1, '2026-03-25 10:20:00');

-- ==============================================================================
-- 8. INSERT DỮ LIỆU MẪU (PRODUCT_IMAGES)
-- ==============================================================================
INSERT INTO `product_images` (`id`, `product_id`, `url`) VALUES
(1, 1, 'https://images.unsplash.com/photo-1556906781-9a412961c28c?auto=format&fit=crop&w=900&q=80'),
(2, 1, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=900&q=80'),
(3, 2, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=900&q=80'),
(4, 2, 'https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?auto=format&fit=crop&w=900&q=80'),
(5, 3, 'https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?auto=format&fit=crop&w=900&q=80'),
(6, 3, 'https://images.unsplash.com/photo-1552346154-21d32810aba3?auto=format&fit=crop&w=900&q=80'),
(7, 4, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?auto=format&fit=crop&w=900&q=80'),
(8, 4, 'https://images.unsplash.com/photo-1597045566677-8cf032ed6634?auto=format&fit=crop&w=900&q=80'),
(9, 5, 'https://images.unsplash.com/photo-1587563871167-1ee9c731aefb?auto=format&fit=crop&w=900&q=80'),
(10, 6, 'https://images.unsplash.com/photo-1518002171953-a080ee817e1f?auto=format&fit=crop&w=900&q=80'),
(11, 7, 'https://images.unsplash.com/photo-1608231387042-66d1773070a5?auto=format&fit=crop&w=900&q=80'),
(12, 8, 'https://images.unsplash.com/photo-1539185441755-769473a23570?auto=format&fit=crop&w=900&q=80'),
(13, 9, 'https://images.unsplash.com/photo-1607522370275-f14206abe5d3?auto=format&fit=crop&w=900&q=80'),
(14, 10, 'https://images.unsplash.com/photo-1579338559194-a162d19bf842?auto=format&fit=crop&w=900&q=80'),
(15, 11, 'https://images.unsplash.com/photo-1515955656352-a1fa3ffcd111?auto=format&fit=crop&w=900&q=80'),
(16, 12, 'https://images.unsplash.com/photo-1552346154-21d32810aba3?auto=format&fit=crop&w=900&q=80'),
(17, 13, 'https://images.unsplash.com/photo-1597045566677-8cf032ed6634?auto=format&fit=crop&w=900&q=80'),
(18, 14, 'https://images.unsplash.com/photo-1560769629-975ec94e6a86?auto=format&fit=crop&w=900&q=80'),
(19, 15, 'https://images.unsplash.com/photo-1575537302964-96cd47c06b1b?auto=format&fit=crop&w=900&q=80'),
(20, 16, 'https://images.unsplash.com/photo-1514989940723-e8e51635b782?auto=format&fit=crop&w=900&q=80');

-- ==============================================================================
-- 9. INSERT DỮ LIỆU MẪU (PRODUCT_SIZES)
-- ==============================================================================
INSERT INTO `product_sizes` (`id`, `product_id`, `size_id`, `stock`) VALUES
(1, 1, 7, 15), (2, 1, 9, 25), (3, 1, 11, 30), (4, 1, 13, 20), (5, 1, 15, 10), (6, 1, 17, 5),
(7, 2, 7, 20), (8, 2, 9, 45), (9, 2, 11, 50), (10, 2, 13, 35), (11, 2, 15, 15),
(12, 3, 9, 8), (13, 3, 11, 12), (14, 3, 13, 10), (15, 3, 15, 6),
(16, 4, 5, 20), (17, 4, 7, 30), (18, 4, 9, 40), (19, 4, 11, 35), (20, 4, 13, 25),
(21, 5, 7, 12), (22, 5, 9, 28), (23, 5, 11, 32), (24, 5, 13, 18),
(25, 6, 3, 15), (26, 6, 5, 25), (27, 6, 7, 35), (28, 6, 9, 40), (29, 6, 11, 20),
(30, 7, 9, 14), (31, 7, 11, 18), (32, 7, 13, 12),
(33, 8, 7, 16), (34, 8, 9, 24), (35, 8, 11, 28), (36, 8, 13, 14),
(37, 9, 9, 10), (38, 9, 11, 15), (39, 9, 13, 12), (40, 9, 15, 8),
(41, 10, 7, 18), (42, 10, 9, 30), (43, 10, 11, 35), (44, 10, 13, 22),
(45, 11, 9, 6), (46, 11, 11, 8), (47, 11, 13, 5),
(48, 12, 9, 7), (49, 12, 11, 9), (50, 12, 13, 6),
(51, 13, 5, 25), (52, 13, 7, 40), (53, 13, 9, 60), (54, 13, 11, 55), (55, 13, 13, 30),
(56, 14, 9, 10), (57, 14, 11, 14), (58, 14, 13, 8),
(59, 15, 7, 15), (60, 15, 9, 22), (61, 15, 11, 25), (62, 15, 13, 18),
(63, 16, 9, 14), (64, 16, 11, 18), (65, 16, 13, 12),
(66, 17, 9, 12), (67, 17, 11, 15), (68, 17, 13, 10),
(69, 18, 5, 20), (70, 18, 7, 30), (71, 18, 9, 35), (72, 18, 11, 25),
(73, 19, 5, 18), (74, 19, 7, 28), (75, 19, 9, 32), (76, 19, 11, 20),
(77, 20, 7, 14), (78, 20, 9, 24), (79, 20, 11, 26), (80, 20, 13, 16),
(81, 21, 5, 30), (82, 21, 7, 40), (83, 21, 9, 50), (84, 21, 11, 45), (85, 21, 13, 25),
(86, 22, 5, 20), (87, 22, 7, 35), (88, 22, 9, 40), (89, 22, 11, 30),
(90, 23, 9, 15), (91, 23, 11, 20), (92, 23, 13, 12),
(93, 24, 7, 12), (94, 24, 9, 22), (95, 24, 11, 25), (96, 24, 13, 15),
(97, 25, 7, 18), (98, 25, 9, 28), (99, 25, 11, 30), (100, 25, 13, 20);

-- ==============================================================================
-- 10. INSERT DỮ LIỆU MẪU (BLOGS)
-- ==============================================================================
INSERT INTO `blogs` (`id`, `title`, `content`, `thumbnail`, `author_id`, `status`, `created_at`) VALUES
(1, 'Kỷ Nguyên Mới Của Bộ Đệm Dynamic Air: Đột Phá Tại Đỉnh Cao Thể Thao', 
'Hãng thể thao số một thế giới vừa chính thức trình làng công nghệ đệm khí Dynamic Air thế hệ mới trên mẫu giày Air Max Dn. Sau hơn ba năm nghiên cứu bí mật tại Trung tâm Đổi mới Sáng tạo Thể thao Nike (NSRL), công nghệ này sử dụng bốn ống trụ khí với hai mức áp suất chênh lệch: 15 psi ở hai ống sau và 5 psi ở hai ống trước.

Sự chênh lệch áp suất này cho phép không khí luân chuyển nhịp nhàng theo từng chuyển động bàn chân từ gót sang mũi, tạo cảm giác mượt mà và êm ái chưa từng thấy trong lịch sử giày thể thao. Đây được đánh giá là bước nhảy vọt quan trọng nhất của dòng Air Max kể từ khi ra mắt bộ đệm VaporMax vào năm 2017.', 
'https://images.unsplash.com/photo-1556906781-9a412961c28c?auto=format&fit=crop&w=700&q=80', 1, 1, '2026-03-01 09:00:00'),

(2, 'Eliud Kipchoge Và Hành Trình Bứt Phá Giới Hạn Marathon 42.195km', 
'Eliud Kipchoge, huyền thoại điền kinh người Kenya, tiếp tục truyền cảm hứng cho hàng triệu chân chạy trên toàn cầu với triết lý bất hủ "No Human Is Limited" (Không con người nào bị giới hạn).

Từ dự án Breaking2 tại đường đua Monza năm 2017 đến kỳ tích chạy 1 giờ 59 phút 40 giây tại Vienna năm 2019, Kipchoge đã cùng đội ngũ kỹ sư Nike đồng hành phát triển các thế hệ siêu giày từ Vaporfly 4% đến Alphafly 3. Bài viết này khám phá chi tiết chế độ dinh dưỡng, giáo án tập luyện và tinh thần thép của vận động viên marathon vĩ đại nhất mọi thời đại.', 
'https://images.unsplash.com/photo-1461896836934-ffe607ba8211?auto=format&fit=crop&w=700&q=80', 1, 1, '2026-03-03 14:30:00'),

(3, 'Hướng Dẫn Phối Đồ Streetwear Đỉnh Cao Cùng Dòng Giày Dunk Cổ Thấp', 
'Dù đã ra mắt từ năm 1985 dành cho giải bóng rổ đại học Mỹ NCAA, Nike Dunk Low ngày nay đã trở thành món đồ thời trang không thể thiếu trong tủ đồ của bất kỳ tín đồ thời trang đường phố nào.

Để phối đồ chuẩn đẹp cùng Dunk Low:
1. Quần Jeans ống rộng (Baggy Jeans): Tạo phong cách phóng khoáng thập niên 90 che nhẹ phần cổ giày.
2. Quần Cargo túi hộp: Tông màu rêu, be hoặc đen làm tôn lên sự mạnh mẽ của đôi giày.
3. Quần Short thể thao phối vớ cổ cao: Lựa chọn hoàn hảo cho mùa hè năng động và khoe trọn vẹn kiểu dáng đôi giày.', 
'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?auto=format&fit=crop&w=700&q=80', 3, 1, '2026-03-05 16:15:00'),

(4, 'Bảo Vệ Và Vệ Sinh Đôi Giày Trắng Air Force 1 Của Bạn Luôn Sáng Bóng', 
'Một đôi giày Air Force 1 Triple White tinh tươm luôn là điểm nhấn hoàn hảo cho mọi trang phục. Tuy nhiên, thời tiết ẩm ướt và khói bụi đô thị có thể khiến đôi giày nhanh chóng ố vàng nếu không được chăm sóc đúng cách.

Các bước bảo quản chuẩn chuyên gia:
- Vệ sinh khô sau mỗi lần sử dụng bằng khăn microfiber mềm.
- Dùng dung dịch vệ sinh giày chuyên dụng bọt tuyết kết hợp bàn chải lông mềm cho phần thân da và bàn chải cứng cho phần đế cao su.
- Dùng cây giữ form giày (Shoe Trees) để hạn chế tối đa các nếp nhăn (creases) ở mũi giày.
- Xịt nano chống thấm nước trước khi ra đường trong những ngày mưa.', 
'https://images.unsplash.com/photo-1579338559194-a162d19bf842?auto=format&fit=crop&w=700&q=80', 2, 1, '2026-03-08 10:20:00'),

(5, 'Công Nghệ ZoomX Và Cuộc Đua Tốc Độ Của Những Đôi Siêu Giày Chạy Bộ', 
'Kể từ khi bọt đệm ZoomX (Pebax) ra đời, các kỷ lục marathon thế giới liên tục bị xô đổ. Với trọng lượng chỉ bằng 1/3 so với mút xốp EVA truyền thống nhưng mang lại tỷ lệ hoàn trả năng lượng lên tới 85%, ZoomX đã thiết lập lại toàn bộ tiêu chuẩn của ngành công nghiệp giày chạy bộ.

Bài viết này phân tích sâu cấu trúc phân tử của bọt đệm Pebax, sự kết hợp ăn ý với thanh đệm carbon toàn phần và lý do vì sao hầu hết các vận động viên podium tại các giải chạy danh giá nhất thế giới đều lựa chọn công nghệ này.', 
'https://images.unsplash.com/photo-1515955656352-a1fa3ffcd111?auto=format&fit=crop&w=700&q=80', 1, 1, '2026-03-12 11:00:00'),

(6, 'Lịch Sử Và Di Sản Huyền Thoại Của Dòng Giày Air Jordan 1', 
'Năm 1984, một tân binh xuất sắc của Chicago Bulls mang tên Michael Jordan đã ký hợp đồng với một hãng thể thao còn non trẻ. Phần còn lại đã trở thành lịch sử. Đôi giày Air Jordan 1 màu đen đỏ từng bị ban lãnh đạo giải đấu NBA phạt 5.000 USD mỗi trận vì vi phạm quy định đồng phục, nhưng chính lệnh cấm đó đã thổi bùng cơn sốt Jordan trên toàn cầu.

Hơn 40 năm trôi qua, Air Jordan 1 vẫn giữ vững vị thế độc tôn là biểu tượng thời trang và văn hóa bóng rổ có sức ảnh hưởng sâu rộng nhất qua nhiều thế hệ.', 
'https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?auto=format&fit=crop&w=700&q=80', 2, 1, '2026-03-15 15:45:00'),

(7, 'Cẩm Nang Chọn Size Giày Thể Thao Chuẩn Xác Từng Milimet', 
'Chọn sai kích cỡ giày không chỉ gây đau đớn, phồng rộp bàn chân mà còn có thể dẫn đến chấn thương gân gót chân Achilles và viêm cân gan chân.

Cách đo kích thước bàn chân tại nhà chuẩn xác nhất:
1. Đặt tờ giấy A4 sát mép tường, đặt gót chân chạm nhẹ vào tường và đứng thẳng người.
2. Đánh dấu điểm dài nhất của ngón chân cái hoặc ngón trỏ lên mặt giấy.
3. Đo chiều dài bằng thước kẻ chính xác theo đơn vị cm.
4. Đối chiếu bảng quy đổi size quốc tế (US, UK, EU, CM). Lưu ý: Đối với giày chạy bộ, luôn cộng thêm 0.5cm đến 1cm để chân có không gian giãn nở khi vận động.', 
'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=700&q=80', 3, 1, '2026-03-18 09:30:00'),

(8, 'Nike ACG: Câu Chuyện Chinh Phục Mọi Địa Hình Dã Ngoại Khắc Nghiệt', 
'Dòng sản phẩm All Conditions Gear (ACG) ra đời vào năm 1989 với sứ mệnh mang lại những thiết bị bền bỉ nhất cho các nhà thám hiểm, leo núi và chạy địa hình.

Với tinh thần thiết kế phong trần hòa quyện vào thiên nhiên, các sản phẩm ACG luôn được thử nghiệm thực tế tại những vùng đất khắc nghiệt nhất như núi lửa Hawaii hay rừng mưa nhiệt đới Oregon. Khám phá mẫu giày Mountain Fly 2 Low mới nhất với công nghệ đế bám đa hướng.', 
'https://images.unsplash.com/photo-1514989940723-e8e51635b782?auto=format&fit=crop&w=700&q=80', 1, 1, '2026-03-21 13:20:00'),

(9, 'Top 5 Mẫu Giày Chạy Bộ Tốt Nhất Năm 2026 Dành Cho Người Mới Bắt Đầu', 
'Bắt đầu bộ môn chạy bộ có thể khiến bạn bối rối trước hàng ngàn mẫu giày khác nhau trên thị trường. Một đôi giày tốt cho người mới cần đáp ứng 3 tiêu chí: Đệm êm vừa phải, độ bền cao và độ ổn định tốt.

Danh sách 5 đôi giày được các chuyên gia khuyên dùng nhiều nhất:
1. Nike Pegasus 41 - Cân bằng hoàn hảo giữa độ êm và độ bền.
2. Asics Novablast 4 - Nảy tốt, tạo hứng khởi khi chạy hàng ngày.
3. New Balance Fresh Foam X 1080v13 - Siêu êm ái cho những bước chạy phục hồi.
4. Puma Velocity Nitro 3 - Bám đường cực đỉnh với cao su PumaGrip.
5. Saucony Ride 17 - Cảm giác chạy tự nhiên và thoải mái tối đa.', 
'https://images.unsplash.com/photo-1560769629-975ec94e6a86?auto=format&fit=crop&w=700&q=80', 2, 1, '2026-03-24 16:50:00'),

(10, 'Bí Quyết Phục Hồi Cơ Bắp Và Bàn Chân Sau Những Chuyến Chạy Dài', 
'Chạy bộ là quá trình phá vỡ cơ bắp và phục hồi để cơ thể trở nên khỏe mạnh hơn. Nếu không chú trọng phục hồi, nguy cơ chấn thương quá tải là rất cao.

Các phương pháp phục hồi được khoa học chứng minh:
- Ngâm chân nước đá lạnh từ 10-15 phút để giảm sưng viêm mô mềm.
- Lăn bóng massage hoặc con lăn bọt (Foam Roller) giải phóng các điểm căng cứng cơ bắp chân.
- Bù điện giải và bổ sung protein trong vòng 30 phút sau khi kết thúc buổi tập.
- Đeo vớ nén y khoa và sử dụng dép phục hồi đệm dày như Nike Calm Slide.', 
'https://images.unsplash.com/photo-1603808033192-082d6919d3e1?auto=format&fit=crop&w=700&q=80', 3, 1, '2026-03-26 10:15:00'),

(11, 'Phong Cách Retro Tennis: Sự Trở Lại Ngoạn Mục Của Adidas Samba', 
'Không còn gói gọn trong các sân bóng đá trong nhà, Adidas Samba và Gazelle đã trở thành biểu tượng thời trang đường phố đình đám nhất trong 3 năm trở lại đây.

Với sự lăng xê nhiệt tình của các ngôi sao toàn cầu như Bella Hadid, Kendall Jenner và Hailey Bieber, phong cách Blokecore kết hợp giữa áo đấu bóng đá cổ điển và quần tây ống rộng cùng đôi Samba mũi da lộn đã càn quét mọi kinh đô thời trang từ London, Paris đến Tokyo.', 
'https://images.unsplash.com/photo-1518002171953-a080ee817e1f?auto=format&fit=crop&w=700&q=80', 2, 0, '2026-03-27 14:00:00'),

(12, 'Đánh Giá Chi Tiết Asics Gel-Kayano 30: Đôi Giày Hỗ Trợ Bàn Chân Bẹt Tốt Nhất', 
'Bàn chân bẹt hoặc lệch trong (overpronation) là nguyên nhân hàng đầu gây đau khớp gối và hội chứng dải chậu chày ở vận động viên chạy bộ. Dòng giày Gel-Kayano của Asics suốt 30 năm qua đã là người bạn đồng hành tin cậy của hàng triệu chân chạy có đặc điểm bàn chân này.

Thế hệ thứ 30 loại bỏ hoàn toàn miếng nhựa cứng chống xoắn truyền thống, thay vào đó là hệ thống đệm thông minh 4D Guidance System tự điều chỉnh theo nhịp chân của bạn, mang lại cảm giác chạy mượt mà tự nhiên mà vẫn giữ vững độ ổn định tuyệt đối.', 
'https://images.unsplash.com/photo-1579338559194-a162d19bf842?auto=format&fit=crop&w=700&q=80', 1, 0, '2026-03-28 17:30:00');

-- ==============================================================================
-- 11. INSERT DỮ LIỆU MẪU (CART_ITEMS)
-- ==============================================================================
INSERT INTO `cart_items` (`id`, `user_id`, `product_id`, `size_id`, `quantity`) VALUES
(1, 4, 1, 9, 1),
(2, 4, 13, 9, 2),
(3, 5, 4, 5, 1),
(4, 5, 6, 7, 1),
(5, 6, 2, 11, 1),
(6, 6, 11, 11, 1),
(7, 7, 3, 11, 1),
(8, 8, 8, 7, 1),
(9, 9, 5, 9, 1),
(10, 10, 21, 5, 2);

SET FOREIGN_KEY_CHECKS = 1;

-- ==============================================================================
-- KIỂM TRA SỐ LƯỢNG BẢN GHI ĐÃ TẠO THÀNH CÔNG
-- ==============================================================================
SELECT 'users' AS TableName, COUNT(*) AS TotalRows FROM `users`
UNION ALL
SELECT 'brands', COUNT(*) FROM `brands`
UNION ALL
SELECT 'categories', COUNT(*) FROM `categories`
UNION ALL
SELECT 'sizes', COUNT(*) FROM `sizes`
UNION ALL
SELECT 'products', COUNT(*) FROM `products`
UNION ALL
SELECT 'product_images', COUNT(*) FROM `product_images`
UNION ALL
SELECT 'product_sizes', COUNT(*) FROM `product_sizes`
UNION ALL
SELECT 'blogs', COUNT(*) FROM `blogs`
UNION ALL
SELECT 'cart_items', COUNT(*) FROM `cart_items`;
