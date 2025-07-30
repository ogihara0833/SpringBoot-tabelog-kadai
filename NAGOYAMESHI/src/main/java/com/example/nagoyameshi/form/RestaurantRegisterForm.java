package com.example.nagoyameshi.form;

import java.time.LocalTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class RestaurantRegisterForm {

    @NotBlank(message = "店舗名を入力してください。")
    private String name;

    @NotBlank(message = "店舗説明を入力してください。")
    private String description;

    @NotNull(message = "カテゴリを選択してください。")
    private Integer categoryId;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime lunchStart;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime lunchEnd;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime dinnerStart;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime dinnerEnd;

    @Min(value = 1, message = "ランチ価格（下限）は1円以上で入力してください。")
    private Integer lunchPriceMin;

    @Min(value = 1, message = "ランチ価格（上限）は1円以上で入力してください。")
    private Integer lunchPriceMax;

    @Min(value = 1, message = "ディナー価格（下限）は1円以上で入力してください。")
    private Integer dinnerPriceMin;

    @Min(value = 1, message = "ディナー価格（上限）は1円以上で入力してください。")
    private Integer dinnerPriceMax;

    @NotBlank(message = "郵便番号を入力してください。")
    private String postalCode;

    @NotBlank(message = "住所を入力してください。")
    private String address;

    @NotBlank(message = "電話番号を入力してください。")
    private String phoneNumber;

    private String holiday;

    private MultipartFile mainImageFile;
    private MultipartFile menuImageFile;

    private String imageName;
    private String menuImageName;
}
