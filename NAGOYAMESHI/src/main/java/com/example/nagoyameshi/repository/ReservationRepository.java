package com.example.nagoyameshi.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    List<Reservation> findByUser(User user);

    List<Reservation> findByRestaurant(Restaurant restaurant);

    List<Reservation> findByRestaurantAndVisitDate(Restaurant restaurant, LocalDate visitDate);

    Reservation findByIdAndUser(Integer id, User user);
}
