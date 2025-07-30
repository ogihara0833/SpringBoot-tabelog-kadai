package com.example.nagoyameshi.repository;



import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    

    // 追加例: emailを使ってユーザーを検索（ログイン処理などで使用）
    User findByEmail(String email);

 // 名前 or メールアドレスを部分一致検索（LIKE）
    public Page<User> findByNameLikeOrEmailLike(String nameKeyword, String emailKeyword, Pageable pageable);

    Optional<User> findByStripeSubscriptionId(String subscriptionId);
    }
