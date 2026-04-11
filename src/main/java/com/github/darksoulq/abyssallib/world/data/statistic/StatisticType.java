package com.github.darksoulq.abyssallib.world.data.statistic;

import net.kyori.adventure.key.Key;

import java.util.Objects;

public record StatisticType(Key id) {

    public Statistic get(Key target) {
        return new Statistic(this, target);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StatisticType(Key id))) return false;
        return Objects.equals(this.id, id);
    }

}