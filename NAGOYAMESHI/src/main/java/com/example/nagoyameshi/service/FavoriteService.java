package com.example.nagoyameshi.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.FavoriteRepository;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    public FavoriteService(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    public Optional<Favorite> findFavoriteById(Integer id) {
        return favoriteRepository.findById(id);
    }

    public Optional<Favorite> findFavoriteByUserAndRestaurant(User user, Restaurant restaurant) {
        return favoriteRepository.findByUserAndRestaurant(user, restaurant);
    }

    public Page<Favorite> findFavoritesByUser(User user, Pageable pageable) {
        return favoriteRepository.findByUser(user, pageable);
    }

    public boolean isFavorite(User user, Restaurant restaurant) {
        return favoriteRepository.findByUserAndRestaurant(user, restaurant).isPresent();
    }

    @Transactional
    public void createFavorite(User user, Restaurant restaurant) {
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setRestaurant(restaurant);
        favoriteRepository.save(favorite);
    }

    @Transactional
    public void deleteFavorite(Favorite favorite) {
        favoriteRepository.delete(favorite);
    }
}
