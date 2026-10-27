package com.example.ShopGiayTheThao.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.ShopGiayTheThao.entity.Blog;
import com.example.ShopGiayTheThao.entity.Brand;
import com.example.ShopGiayTheThao.entity.CartItem;
import com.example.ShopGiayTheThao.entity.Category;
import com.example.ShopGiayTheThao.entity.Product;
import com.example.ShopGiayTheThao.entity.Size;
import com.example.ShopGiayTheThao.entity.User;
import com.example.ShopGiayTheThao.repository.BlogRepository;
import com.example.ShopGiayTheThao.repository.BrandRepository;
import com.example.ShopGiayTheThao.repository.CartItemRepository;
import com.example.ShopGiayTheThao.repository.CategoryRepository;
import com.example.ShopGiayTheThao.repository.ProductRepository;
import com.example.ShopGiayTheThao.repository.SizeRepository;
import com.example.ShopGiayTheThao.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final SizeRepository sizeRepository;
    private final ProductRepository productRepository;
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    public DataInitializer(BrandRepository brandRepository,
                           CategoryRepository categoryRepository,
                           SizeRepository sizeRepository,
                           ProductRepository productRepository,
                           BlogRepository blogRepository,
                           UserRepository userRepository,
                           CartItemRepository cartItemRepository) {
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.sizeRepository = sizeRepository;
        this.productRepository = productRepository;
        this.blogRepository = blogRepository;
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public void run(String... args) {
        try {
            seedData();
        } catch (Exception e) {
            System.err.println("Notice during DataInitializer execution: " + e.getMessage());
        }
    }

    private void seedData() {
        if (brandRepository.count() > 0 && productRepository.count() > 0) {
            return; // Already initialized
        }

        // 1. Brands
        Brand nsw = brandRepository.save(new Brand("NIKE SPORTSWEAR", "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=800&q=80",
                "Trọng tâm của mọi công nghệ đột phá từ đệm Air Max, ZoomX đến Flyknit. Sự kết hợp hoàn mỹ giữa thể thao đỉnh cao và phong cách thời trang đương đại."));
        Brand jordan = brandRepository.save(new Brand("JORDAN BRAND", "https://images.unsplash.com/photo-1516478177764-9fe5bd7e9717?auto=format&fit=crop&w=800&q=80",
                "Kế thừa di sản huyền thoại của số 23 Michael Jordan. Những mẫu giày kinh điển Air Jordan 1 đến 39 làm say đắm hàng triệu sneakerhead toàn thế giới."));
        Brand acg = brandRepository.save(new Brand("NIKE ACG", "https://images.unsplash.com/photo-1502680390469-be75c86b636f?auto=format&fit=crop&w=800&q=80",
                "All Conditions Gear — thiết kế dành riêng cho điều kiện thời tiết khắc nghiệt. Khả năng chống thấm GORE-TEX và độ bám siêu hạng trên mọi vách đá hiểm trở."));
        Brand sb = brandRepository.save(new Brand("NIKE SB", "https://images.unsplash.com/photo-1520072959219-c595dc870360?auto=format&fit=crop&w=800&q=80",
                "Dòng sản phẩm chuyên biệt cho văn hóa Skateboarding với lưỡi gà dày êm, đệm Zoom Air hỗ trợ tiếp đất và chất liệu da lộn bền chắc chống rách khi tiếp xúc ván."));
        Brand pro = brandRepository.save(new Brand("NIKE PRO & GYM", "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?auto=format&fit=crop&w=800&q=80",
                "Trang phục nén cơ và giày tập luyện cường độ cao Metcon. Điểm tựa vững chắc cho các vận động viên thể hình, Crossfit và thể lực chuyên nghiệp."));
        Brand converse = brandRepository.save(new Brand("CONVERSE", "https://images.unsplash.com/photo-1607522370275-f14206abe5d3?auto=format&fit=crop&w=800&q=80",
                "Thương hiệu biểu tượng thành viên trực thuộc tập đoàn Nike. Dòng giày Chuck Taylor All Star và One Star gắn liền với âm nhạc, hội họa và thanh xuân."));

        // 2. Categories
        Category catMen = categoryRepository.save(new Category("Giày Nam Thể Thao", "Giày thể thao nam năng động", 1));
        Category catRun = categoryRepository.save(new Category("Giày Chạy Bộ", "Giày chạy êm ái chống sốc", 1));
        Category catLife = categoryRepository.save(new Category("Giày Phong Cách Sống", "Thời trang thường ngày", 1));

        // 3. Sizes
        Size s39 = sizeRepository.save(new Size("EU 39"));
        Size s40 = sizeRepository.save(new Size("EU 40"));
        Size s405 = sizeRepository.save(new Size("EU 40.5"));
        Size s41 = sizeRepository.save(new Size("EU 41"));
        Size s42 = sizeRepository.save(new Size("EU 42"));

        // 4. Products
        Product p1 = productRepository.save(new Product("Nike Air Max Pulse Roam", "Trải nghiệm bộ đệm Dynamic Air thế hệ mới mang lại năng lượng vô tận cho từng sải bước.",
                new BigDecimal("4499000"), null, "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=700&q=80",
                catMen, nsw, 1));

        Product p2 = productRepository.save(new Product("Nike Dunk Low Retro", "Biểu tượng bóng rổ đường phố thập niên 80 trở lại với các phối màu kinh điển.",
                new BigDecimal("3599000"), new BigDecimal("2699000"), "https://images.unsplash.com/photo-1600185365483-26d7a4cc7519?auto=format&fit=crop&w=700&q=80",
                catLife, sb, 1));

        Product p3 = productRepository.save(new Product("Nike Pegasus 41 GORE-TEX", "Được chế tác với màng chống thấm nước GORE-TEX cao cấp sẵn sàng vượt mưa gió.",
                new BigDecimal("3829000"), null, "https://images.unsplash.com/photo-1608231387042-66d1773070a5?auto=format&fit=crop&w=700&q=80",
                catRun, nsw, 1));

        Product p4 = productRepository.save(new Product("Air Jordan 1 Low OG", "Huyền thoại sân bóng rổ với chất liệu da cao cấp và logo Wings biểu tượng.",
                new BigDecimal("3899000"), null, "https://images.unsplash.com/photo-1516478177764-9fe5bd7e9717?auto=format&fit=crop&w=700&q=80",
                catMen, jordan, 1));

        Product p5 = productRepository.save(new Product("Nike ACG Mountain Fly 2 Low", "Chinh phục mọi địa hình hiểm trở với đế gai bám dính siêu hạng và đệm React.",
                new BigDecimal("4109000"), null, "https://images.unsplash.com/photo-1502680390469-be75c86b636f?auto=format&fit=crop&w=700&q=80",
                catRun, acg, 1));

        Product p6 = productRepository.save(new Product("Nike SB Dunk Low Pro White Gum", "Thiết kế dành riêng cho skater chuyên nghiệp với lưỡi gà dày và đệm Zoom Air.",
                new BigDecimal("2779000"), null, "https://images.unsplash.com/photo-1520072959219-c595dc870360?auto=format&fit=crop&w=700&q=80",
                catLife, sb, 1));

        // 5. Default User
        User user = userRepository.findByEmail("customer@nike.vn").orElseGet(() ->
                userRepository.save(new User("Nguyễn Văn A", "customer@nike.vn", "123456", "0901234567", "Hà Nội", "CUSTOMER", 1))
        );

        // 6. Blogs
        blogRepository.save(new Blog("Kỷ Nguyên Mới Của Bộ Đệm Dynamic Air: Đột Phá Tại Đỉnh Cao",
                "Tìm hiểu cách các kỹ sư tại phòng thí nghiệm thể thao Nike NSRL tạo ra dòng chảy áp suất kép hoàn hảo trong từng bước chạy.",
                "https://images.unsplash.com/photo-1556906781-9a412961c28c?auto=format&fit=crop&w=700&q=80",
                user, 1));

        blogRepository.save(new Blog("Eliud Kipchoge Và Hành Trình Bứt Phá Giới Hạn Marathon",
                "Hành trình cùng đôi Alphafly Next% và bài học sâu sắc về sức mạnh ý chí không bao giờ chấp nhận giới hạn bản thân.",
                "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?auto=format&fit=crop&w=700&q=80",
                user, 1));

        blogRepository.save(new Blog("Hướng Dẫn Phối Đồ Streetwear Cùng Dòng Giày Dunk Cổ Thấp",
                "Bí quyết phối hợp sắc sảo giữa quần ống thụng cargo, tất cổ cao và đôi giày kinh điển mang dấu ấn thời đại đường phố.",
                "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?auto=format&fit=crop&w=700&q=80",
                user, 1));

        blogRepository.save(new Blog("Khám Phá Dòng Giày Chạy Địa Hình ACG Zegama Trail",
                "Kiểm nghiệm thực tế trên những triền dốc đá trơn trượt của dãy Alps để mang lại độ tin cậy tuyệt đối cho người chạy trail.",
                "https://images.unsplash.com/photo-1502680390469-be75c86b636f?auto=format&fit=crop&w=700&q=80",
                user, 1));

        blogRepository.save(new Blog("Bảo Vệ Đôi Giày Trắng Air Force 1 Của Bạn Luôn Sáng Bóng",
                "Các bước vệ sinh giày thể thao chuẩn chuyên gia Nike: từ bàn chải lông mềm, dung dịch bọt bọt cho đến cách phơi khô tự nhiên.",
                "https://images.unsplash.com/photo-1579338559194-a162d19bf842?auto=format&fit=crop&w=700&q=80",
                user, 1));

        blogRepository.save(new Blog("Lịch Sử Đằng Sau Đôi Giày \"Bị Cấm\" Air Jordan 1 1985",
                "Nhìn lại câu chuyện huyền thoại làm rung chuyển giải đấu NBA và khởi đầu cho đế chế giày thể thao có sức ảnh hưởng nhất lịch sử.",
                "https://images.unsplash.com/photo-1516478177764-9fe5bd7e9717?auto=format&fit=crop&w=700&q=80",
                user, 1));

        blogRepository.save(new Blog("Đột Phá Công Nghệ Sợi Flyknit: Nhẹ Hơn, Bền Hơn, Bền Vững Hơn",
                "Quy trình dệt liền mạch từng sợi polyester tái chế giúp giảm 60% chất thải so với quy trình cắt may truyền thống.",
                "https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?auto=format&fit=crop&w=700&q=80",
                user, 1));

        blogRepository.save(new Blog("Bí Quyết Chọn Giày Thể Thao Phù Hợp Với Từng Dáng Bàn Chân",
                "Phân tích dáng bàn chân phẳng, vòm cao hay vòm chuẩn để tìm ra đôi giày tối ưu hóa khả năng tiếp đất và giảm chấn thương.",
                "https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?auto=format&fit=crop&w=700&q=80",
                user, 1));

        blogRepository.save(new Blog("Văn Hóa Sưu Tầm Sneaker: Từ Niềm Đam Mê Đến Nghệ Thuật Đầu Tư",
                "Khám phá thị trường sneaker resale toàn cầu và lý do vì sao những phiên bản hợp tác giới hạn lại có giá trị tăng vọt.",
                "https://images.unsplash.com/photo-1515955656352-a1fa3ffcd111?auto=format&fit=crop&w=700&q=80",
                user, 1));

        // 7. Initial Cart Items
        if (cartItemRepository.count() == 0) {
            cartItemRepository.save(new CartItem(user, p1, s405, 1));
            cartItemRepository.save(new CartItem(user, p2, s41, 1));
            cartItemRepository.save(new CartItem(user, p3, s405, 1));
        }
    }
}
