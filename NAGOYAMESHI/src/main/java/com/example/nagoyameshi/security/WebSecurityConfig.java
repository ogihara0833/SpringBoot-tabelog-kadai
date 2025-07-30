package com.example.nagoyameshi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration 
@EnableWebSecurity 
@EnableMethodSecurity 
public class WebSecurityConfig {


        // セキュリティ設定の中心となるメソッド（リクエストごとのアクセス制御など）
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        	
            .authorizeHttpRequests(authz -> authz

                // 誰でもアクセス可能な静的リソースや公開ページ
                .requestMatchers("/", "/login", "/signup/**","/signup","/reset-password/**", "/css/**", "/js/**", "/images/**","/storage/**", "/restaurants", "/restaurants/{id}","/restaurants/*/reviews/**").permitAll()

                // 管理者専用：/admin配下はADMINロールだけがアクセス可能
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // 有料会員専用：/premium配下はPREMIUM_USERロールだけがアクセス可能
                .requestMatchers("/premium/**","/restaurants/*/reviews/register", "/restaurants/*/reviews/create","/reservations/**").hasRole("PREMIUM")

                // どの会員種別でもログインしてさえいればOK！アクセス可能
                .anyRequest().authenticated()

            )
            // フォームログインの設定
            .formLogin(form -> form
                .loginPage("/login")                // カスタムログインページ
                .loginProcessingUrl("/login")       // POSTログイン処理のURL（Thymeleafのformアクション）
                .defaultSuccessUrl("/?loggedIn", true)       // ログイン成功時のリダイレクト先
                .failureUrl("/login?error")         // ログイン失敗時のリダイレクト先
                .permitAll()                        // 全ユーザーがアクセス可能
            )
            // ログアウト設定
            .logout(logout -> logout
                .logoutUrl("/logout")                     // ログアウトのURL
                .logoutSuccessUrl("/?loggedOut")       // ログアウト成功後の画面
                .permitAll()
            )
            // CSRFの一部無効化（StripeのWebhookなど、外部からPOSTを受けるURLに対して）
            .csrf(csrf -> csrf.ignoringRequestMatchers("/stripe/webhook"));

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
