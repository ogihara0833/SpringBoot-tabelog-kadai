package com.example.nagoyameshi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReviewEditForm;
import com.example.nagoyameshi.form.ReviewRegisterForm;
import com.example.nagoyameshi.repository.ReviewRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Optional<Review> findReviewById(Integer id) {
        return reviewRepository.findById(id);
    }

    public List<Review> findTop6ReviewsByRestaurantOrderByCreatedAtDesc(Restaurant restaurant) {
        return reviewRepository.findTop6ByRestaurantOrderByCreatedAtDesc(restaurant);
    }

    public Review findReviewByRestaurantAndUser(Restaurant restaurant, User user) {
        return reviewRepository.findByRestaurantAndUser(restaurant, user);
    }

    public long countReviewsByRestaurant(Restaurant restaurant) {
        return reviewRepository.countByRestaurant(restaurant);
    }

    public Page<Review> findReviewsByRestaurantOrderByCreatedAtDesc(Restaurant restaurant, Pageable pageable) {
        return reviewRepository.findByRestaurantOrderByCreatedAtDesc(restaurant, pageable);
    }

    @Transactional
    public void createReview(ReviewRegisterForm reviewRegisterForm, Restaurant restaurant, User user) {
        Review review = new Review();
        review.setRestaurant(restaurant);
        review.setUser(user);
        review.setScore(reviewRegisterForm.getScore());
        review.setContent(reviewRegisterForm.getContent());

        reviewRepository.save(review);
    }

    @Transactional
    public void updateReview(ReviewEditForm reviewEditForm, Review review) {
        review.setScore(reviewEditForm.getScore());
        review.setContent(reviewEditForm.getContent());

        reviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(Review review) {
        reviewRepository.delete(review);
    }

    public boolean hasUserAlreadyReviewed(Restaurant restaurant, User user) {
        return reviewRepository.findByRestaurantAndUser(restaurant, user) != null;
    }
}
