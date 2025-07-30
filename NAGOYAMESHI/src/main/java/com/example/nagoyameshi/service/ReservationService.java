package com.example.nagoyameshi.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.ReservationRepository;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public void saveReservation(Reservation reservation) {
        reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsByUser(User user) {
        return reservationRepository.findByUser(user);
    }

    public void cancelReservation(Integer reservationId, User user) {
        Reservation reservation = reservationRepository.findByIdAndUser(reservationId, user);
        if (reservation != null) {
            reservationRepository.delete(reservation);
        }
    }
    
    public Optional<Reservation> findById(Integer reservationId) {
        return reservationRepository.findById(reservationId);
    }
    
    public List<Reservation> getReservationsByUserSortedByVisitDate(User user) {
        return reservationRepository.findByUser(user).stream()
            .sorted(Comparator.comparing(Reservation::getVisitDate).reversed())
            .toList();
    }

}
