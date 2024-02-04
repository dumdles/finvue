package com.sp.finvue;

import androidx.annotation.NonNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class ThisLocalizedWeek {
    private final static ZoneId TZ = ZoneId.of("Pacific/Auckland");
    private final Locale locale;
    private final DayOfWeek firstDayofWeek;
    private final DayOfWeek lastDayofWeek;

    public ThisLocalizedWeek(Locale locale) {
        this.locale = locale;
        this.firstDayofWeek = WeekFields.of(locale).getFirstDayOfWeek();
        this.lastDayofWeek = DayOfWeek.of(((this.firstDayofWeek.getValue() + 5) % DayOfWeek.values().length) + 1);
    }

    public LocalDate getFirstDay() {
        return LocalDate.now(TZ).with(TemporalAdjusters.previousOrSame(this.firstDayofWeek));
    }

    public LocalDate getLastDay() {
        return LocalDate.now(TZ).with(TemporalAdjusters.nextOrSame(this.lastDayofWeek));
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("The $s week starts on %s and ends on %s",
                this.locale.getDisplayName(),
                this.firstDayofWeek,
                this.lastDayofWeek
        );
    }
}
