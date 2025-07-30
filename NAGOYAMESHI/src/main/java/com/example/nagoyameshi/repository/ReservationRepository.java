package com.example.nagoyameshi.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {


    // 特定ユーザーの予約一覧
    List<Reservation> findByUser(User user);

    // 特定店舗の予約一覧
    List<Reservation> findByRestaurant(Restaurant restaurant);

    // 特定店舗＆来店日の予約
    List<Reservation> findByRestaurantAndVisitDate(Restaurant restaurant, LocalDate visitDate);

    // キャンセルやチェック用に1件取得（店舗・日付・人数など複合条件があれば追記できる）
    Reservation findByIdAndUser(Integer id, User user);
}
