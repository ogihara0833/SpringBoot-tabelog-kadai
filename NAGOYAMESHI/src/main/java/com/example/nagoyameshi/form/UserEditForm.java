package com.example.nagoyameshi.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UserEditForm {

    @NotNull
    private Integer id;

    @NotBlank(message = "氏名を入力してください。")
    private String name;

    @NotBlank(message = "電話番号を入力してください。")
    private String phoneNumber;

    @NotBlank(message = "メールアドレスを入力してください。")
    private String email;
}
