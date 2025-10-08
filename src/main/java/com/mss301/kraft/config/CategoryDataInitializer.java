package com.mss301.kraft.config;

import com.mss301.kraft.product_service.entity.Category;
import com.mss301.kraft.product_service.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryDataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() == 0) {
            log.info("Initializing categories...");

            List<String> categoryNames = Arrays.asList(
                "Nón 1/2",
                "Nón 3/4",
                "Nón bảo hiểm",
                "Phụ kiện",
                "Găng tay",
                "Thời trang",
                "Túi balo",
                "Kính và mái che",
                "Giày",
                "Bestseller",
                "Combo Deal"
            );

            for (String name : categoryNames) {
                Category category = new Category();
                category.setName(name);
                categoryRepository.save(category);
                log.info("Created category: {}", name);
            }

            log.info("Categories initialization completed!");
        } else {
            log.info("Categories already exist, skipping initialization");
        }
    }
}
