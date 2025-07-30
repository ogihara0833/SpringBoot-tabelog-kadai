package com.example.nagoyameshi.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "restaurants")
@ToString(exclude = "reservations")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Reservation> reservations;

    @Column(name = "name")
    private String name;

    @Column(name = "image_name")
    private String imageName;

    @Column(name = "menu_image_name")
    private String menuImageName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "address")
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "holiday")
    private String holiday;

    @Column(name = "lunch_start")
    private LocalTime lunchStart;

    @Column(name = "lunch_end")
    private LocalTime lunchEnd;


    @Column(name = "dinner_start")
    private LocalTime dinnerStart;

    @Column(name = "dinner_end")
    private LocalTime dinnerEnd;


    @Column(name = "lunch_price_min")
    private Integer lunchPriceMin;

    @Column(name = "lunch_price_max")
    private Integer lunchPriceMax;


    @Column(name = "dinner_price_min")
    private Integer dinnerPriceMin;

    @Column(name = "dinner_price_max")
    private Integer dinnerPriceMax;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.REMOVE)
    private List<Review> reviews;
    
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;
}
