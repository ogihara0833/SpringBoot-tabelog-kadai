package com.example.nagoyameshi.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.example.nagoyameshi.constant.MembershipType;

import lombok.Data;

@Data
public class SignupForm {

    @NotBlank(message = "氏名を入力してください。")
    private String name;

    @NotBlank(message = "メールアドレスを入力してください。")
    @Email(message = "正しいメールアドレスの形式で入力してください。")
    private String email;

    @NotBlank(message = "電話番号を入力してください。")
    private String phoneNumber;

    @NotBlank(message = "パスワードを入力してください。")
    @Size(min = 8, message = "パスワードは8文字以上で入力してください。")
    private String password;

    @NotBlank(message = "確認用パスワードを入力してください。")
    private String passwordConfirmation;

    @NotNull(message = "会員種別を選択してください。")
    private MembershipType membershipType; 
}
