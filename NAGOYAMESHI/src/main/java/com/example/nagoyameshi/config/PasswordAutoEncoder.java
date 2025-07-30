
package com.example.nagoyameshi.config; 

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.UserRepository; 

@Component // Spring Boot起動時に自動的にこのクラスを実行対象にする
public class PasswordAutoEncoder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    // コンストラクタで依存注入（DI）
    public PasswordAutoEncoder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // アプリケーション起動時に一度だけ実行されるメソッド
    @Override
    public void run(String... args) {
        // 全ユーザーを取得
        List<User> users = userRepository.findAll();

        for (User user : users) {
            String password = user.getPassword();

            
            if (!password.startsWith("$2a$")) {
                
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);

            // ★ パスワードがまだハッシュ化されていない場合のみ実行
            // BCrypt のハッシュは $2a$, $2b$, $2y$ などで始まる（主に $2a$）
            if (!password.startsWith("$2a$")) {
                // ハッシュ化して再保存
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);

                // ログ出力（確認用）
                System.out.println("✅ ハッシュ化完了: " + user.getEmail());
            	}
            }
        }
    }
}
