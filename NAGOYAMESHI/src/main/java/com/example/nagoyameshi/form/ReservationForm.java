package com.example.nagoyameshi.form;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ReservationForm {

    @NotNull(message = "店舗IDは必須です")
    private Integer restaurantId;

    @NotNull(message = "予約日は必須です")
    @FutureOrPresent(message = "予約日は本日以降を選択してください")
    private LocalDate visitDate;

    @NotNull(message = "人数を入力してください")
    @Min(value = 1, message = "1人以上で予約してください")
    private Integer numberOfPeople;
    
    @NotNull(message = "予約時間を選択してください")
    private LocalTime visitTime;
}
