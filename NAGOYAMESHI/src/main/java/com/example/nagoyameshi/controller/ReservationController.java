package com.example.nagoyameshi.controller;

import java.time.LocalTime;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReservationForm;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.ReservationService;
import com.example.nagoyameshi.service.RestaurantService;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final RestaurantService restaurantService;

    public ReservationController(ReservationService reservationService, RestaurantService restaurantService) {
        this.reservationService = reservationService;
        this.restaurantService = restaurantService;
    }

    @GetMapping("/new")
    public String showForm(@RequestParam("restaurantId") Integer restaurantId,
                           @AuthenticationPrincipal UserDetailsImpl principal,
                           Model model) {

        User user = principal.getUser();
        if (!user.isPremium()) {
            return "error/access-denied";
        }

        ReservationForm form = new ReservationForm();
        form.setRestaurantId(restaurantId);
        model.addAttribute("reservationForm", form);
        Restaurant restaurant = restaurantService.findById(restaurantId)
        	    .orElseThrow(() -> new IllegalArgumentException("店舗が見つかりません"));
        	model.addAttribute("restaurant", restaurant);
        return "reservations/form";
    }

    @PostMapping
    public String createReservation(@ModelAttribute @Valid ReservationForm form,
                                    BindingResult result,
                                    @AuthenticationPrincipal UserDetailsImpl principal,
                                    Model model) {

        User user = principal.getUser();
        Restaurant restaurant = restaurantService.findById(form.getRestaurantId())
                .orElseThrow(() -> new IllegalArgumentException("店舗が見つかりません"));

        if (!user.isPremium()) {
            return "error/access-denied";
        }

        if (result.hasErrors()) {
            model.addAttribute("restaurant", restaurant);
            return "reservations/confirm";
        }

        LocalTime time = form.getVisitTime();

     LocalTime lunchStart = restaurant.getLunchStart();
     LocalTime lunchEnd = restaurant.getLunchEnd();
     LocalTime dinnerStart = restaurant.getDinnerStart();
     LocalTime dinnerEnd = restaurant.getDinnerEnd();

     boolean isWithinLunch = lunchStart != null && lunchEnd != null
                           && time != null
                           && time.isAfter(lunchStart) && time.isBefore(lunchEnd);

     boolean isWithinDinner = dinnerStart != null && dinnerEnd != null
                            && time != null
                            && time.isAfter(dinnerStart) && time.isBefore(dinnerEnd);

     
     if (!(isWithinLunch || isWithinDinner)) {
         result.rejectValue("visitTime", null, "予約は営業時間内に限ります");
         model.addAttribute("restaurant", restaurant);
         return "reservations/confirm";
     }


        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setRestaurant(restaurant);
        reservation.setVisitDate(form.getVisitDate());
        reservation.setVisitTime(form.getVisitTime());
        reservation.setNumberOfPeople(form.getNumberOfPeople());
        reservationService.saveReservation(reservation);

        return "redirect:/reservations/complete";
    }

    @GetMapping("/complete")
    public String showComplete() {
        return "reservations/complete";
    }

    @GetMapping
    public String showReservationList(@AuthenticationPrincipal UserDetailsImpl principal,
                                      Model model) {

        User user = principal.getUser();
        if (!user.isPremium()) {
            return "error/access-denied";
        }
        
        List<Reservation> sorted = reservationService.getReservationsByUserSortedByVisitDate(user);
        model.addAttribute("reservations", sorted);

        return "reservations/list";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Integer reservationId,
                               @AuthenticationPrincipal UserDetailsImpl principal,
                               Model model) {

        User user = principal.getUser();
        if (!user.isPremium()) {
            return "error/access-denied";
        }

        Reservation reservation = reservationService.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("予約が見つかりません"));

        if (!reservation.getUser().getId().equals(user.getId())) {
            return "error/access-denied";
        }

        Restaurant restaurant = reservation.getRestaurant();

        ReservationForm form = new ReservationForm();
        form.setRestaurantId(restaurant.getId());
        form.setVisitDate(reservation.getVisitDate());
        form.setVisitTime(reservation.getVisitTime());
        form.setNumberOfPeople(reservation.getNumberOfPeople());

        model.addAttribute("reservationForm", form);
        model.addAttribute("reservation", reservation);
        model.addAttribute("restaurant", restaurant);

        return "reservations/edit";
    }

    @PostMapping("/update")
    public String updateReservation(@ModelAttribute @Valid ReservationForm form,
                                    BindingResult result,
                                    @RequestParam("reservationId") Integer reservationId,
                                    @AuthenticationPrincipal UserDetailsImpl principal,
                                    Model model) {

        User user = principal.getUser();
        if (!user.isPremium()) {
            return "error/access-denied";
        }

        Reservation reservation = reservationService.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("予約が見つかりません"));
        Restaurant restaurant = restaurantService.findById(form.getRestaurantId())
                .orElseThrow(() -> new IllegalArgumentException("店舗が見つかりません"));

        if (result.hasErrors()) {
            model.addAttribute("reservation", reservation);
            model.addAttribute("restaurant", restaurant);
            return "reservations/edit";
        }

        boolean isWithinLunch = form.getVisitTime().isAfter(restaurant.getLunchStart())
                             && form.getVisitTime().isBefore(restaurant.getLunchEnd());
        boolean isWithinDinner = form.getVisitTime().isAfter(restaurant.getDinnerStart())
                             && form.getVisitTime().isBefore(restaurant.getDinnerEnd());

        if (!(isWithinLunch || isWithinDinner)) {
            result.rejectValue("visitTime", null, "営業時間外の時間は選べません");
            model.addAttribute("reservation", reservation);
            model.addAttribute("restaurant", restaurant);
            return "reservations/edit";
        }

        reservation.setVisitDate(form.getVisitDate());
        reservation.setVisitTime(form.getVisitTime());
        reservation.setNumberOfPeople(form.getNumberOfPeople());
        reservationService.saveReservation(reservation);

        return "redirect:/reservations";
    }

    @PostMapping("/cancel")
    public String cancelReservation(@RequestParam("reservationId") Integer reservationId,
                                    @AuthenticationPrincipal UserDetailsImpl principal) {

        User user = principal.getUser();
        if (!user.isPremium()) {
            return "error/access-denied";
        }

        reservationService.cancelReservation(reservationId, user);
        return "redirect:/reservations";
    }

    @GetMapping("/detail")
    public String showReservationDetail(@RequestParam("id") Integer reservationId,
                                        @AuthenticationPrincipal UserDetailsImpl principal,
                                        Model model) {

        User user = principal.getUser();
        if (!user.isPremium()) {
            return "error/access-denied";
        }

        Reservation reservation = reservationService.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("予約が見つかりません"));

        if (!reservation.getUser().getId().equals(user.getId())) {
            return "error/access-denied";
        }

        Restaurant restaurant = reservation.getRestaurant();
        model.addAttribute("reservation", reservation);
        model.addAttribute("restaurant", restaurant);

        return "reservations/detail";
    }
}
