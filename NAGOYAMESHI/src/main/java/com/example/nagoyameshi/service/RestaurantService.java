package com.example.nagoyameshi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.form.RestaurantEditForm;
import com.example.nagoyameshi.form.RestaurantRegisterForm;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;


    // 保存先の絶対パス
    private static final String UPLOAD_DIR = "src/main/resources/static/storage/";

    public RestaurantService(
        RestaurantRepository restaurantRepository,
        CategoryRepository categoryRepository
    ) {
        this.restaurantRepository = restaurantRepository;
        this.categoryRepository = categoryRepository;
    }
    
    public Optional<Restaurant> findById(Integer id) {
        return restaurantRepository.findById(id);
    }

    @Transactional
    public void create(RestaurantRegisterForm form) {
        Restaurant restaurant = new Restaurant();


        // メイン画像保存

        if (!form.getMainImageFile().isEmpty()) {
            String hashedName = generateNewFileName(form.getMainImageFile().getOriginalFilename());
            Path filePath = Paths.get(UPLOAD_DIR + hashedName);
            saveFile(form.getMainImageFile(), filePath);
            restaurant.setImageName(hashedName);
        }


        // メニュー画像保存

        if (!form.getMenuImageFile().isEmpty()) {
            String hashedName = generateNewFileName(form.getMenuImageFile().getOriginalFilename());
            Path filePath = Paths.get(UPLOAD_DIR + hashedName);
            saveFile(form.getMenuImageFile(), filePath);
            restaurant.setMenuImageName(hashedName);
        }


        // カテゴリ設定
        restaurant.setCategory(
            categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("カテゴリが存在しません"))
        );


        // その他フィールド設定
        restaurant.setName(form.getName());
        restaurant.setDescription(form.getDescription());
        restaurant.setPostalCode(form.getPostalCode());
        restaurant.setAddress(form.getAddress());
        restaurant.setPhoneNumber(form.getPhoneNumber());
        restaurant.setLunchStart(form.getLunchStart());
        restaurant.setLunchEnd(form.getLunchEnd());
        restaurant.setDinnerStart(form.getDinnerStart());
        restaurant.setDinnerEnd(form.getDinnerEnd());
        restaurant.setHoliday(form.getHoliday());


        restaurant.setLunchPriceMin(form.getLunchPriceMin());
        restaurant.setLunchPriceMax(form.getLunchPriceMax());
        restaurant.setDinnerPriceMin(form.getDinnerPriceMin());
        restaurant.setDinnerPriceMax(form.getDinnerPriceMax());

        restaurantRepository.save(restaurant);
    }

    @Transactional
    public void update(RestaurantEditForm form) {
        Restaurant restaurant = restaurantRepository.getReferenceById(form.getId());


        restaurant.setCategory(categoryRepository.getReferenceById(form.getCategoryId()));
        restaurant.setName(form.getName());
        restaurant.setDescription(form.getDescription());
        restaurant.setPostalCode(form.getPostalCode());
        restaurant.setAddress(form.getAddress());
        restaurant.setPhoneNumber(form.getPhoneNumber());
        restaurant.setHoliday(form.getHoliday());
        restaurant.setLunchStart(form.getLunchStart());
        restaurant.setLunchEnd(form.getLunchEnd());
        restaurant.setDinnerStart(form.getDinnerStart());
        restaurant.setDinnerEnd(form.getDinnerEnd());
        restaurant.setLunchPriceMin(form.getLunchPriceMin());
        restaurant.setLunchPriceMax(form.getLunchPriceMax());
        restaurant.setDinnerPriceMin(form.getDinnerPriceMin());
        restaurant.setDinnerPriceMax(form.getDinnerPriceMax());
        restaurant.setIsFeatured(Boolean.TRUE.equals(form.getIsFeatured()));

        // メイン画像処理
        String mainImageName = storeImageOrFallback(form.getMainImageFile(), form.getImageName());
        restaurant.setImageName(mainImageName);

        // メニュー画像処理

        String menuImageName = storeImageOrFallback(form.getMenuImageFile(), form.getMenuImageName());
        restaurant.setMenuImageName(menuImageName);

        restaurantRepository.save(restaurant);
    }


    // ✅ 画像保存 or 保持の共通処理
    private String storeImageOrFallback(MultipartFile file, String fallbackName) {
        if (file != null && !file.isEmpty()) {
            String fileName = generateNewFileName(file.getOriginalFilename());
            Path path = Paths.get(UPLOAD_DIR + fileName);
            saveFile(file, path);
            return fileName;
        }
        return fallbackName;
    }


    // ファイル名をUUIDで生成（拡張子保持）
    public String generateNewFileName(String fileName) {
        String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
        return UUID.randomUUID().toString() + "." + ext;
    }

    // ファイル保存処理
    public void saveFile(MultipartFile file, Path path) {
        try {
            Files.createDirectories(path.getParent()); // storage フォルダがなければ作成

            Files.copy(file.getInputStream(), path);
            
            Path deployPath = Paths.get("target/classes/static/storage/", path.getFileName().toString());
            Files.createDirectories(deployPath.getParent());
            Files.copy(file.getInputStream(), deployPath, StandardCopyOption.REPLACE_EXISTING);
            
        } catch (IOException e) {
            throw new RuntimeException("ファイル保存に失敗しました: " + path.toString(), e);
        }
    }
    
    public Page<Restaurant> findAll(Pageable pageable) {
        return restaurantRepository.findAllByOrderByIdAsc(pageable);
    }

    public Page<Restaurant> searchByName(String keyword, Pageable pageable) {
        return restaurantRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }

}
