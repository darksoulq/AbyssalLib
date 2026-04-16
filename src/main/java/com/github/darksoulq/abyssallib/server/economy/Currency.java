package com.github.darksoulq.abyssallib.server.economy;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public record Currency(Key id, Component name, Component pluralName, Component symbol, int fractionalDigits, boolean isPrefix, BigDecimal minBalance, BigDecimal maxBalance) {
    public BigDecimal formatValue(BigDecimal amount) {
        return amount.setScale(fractionalDigits, RoundingMode.HALF_UP);
    }

    public Component format(BigDecimal amount) {
        String formatted = NumberFormat.getInstance(Locale.ROOT).format(formatValue(amount));
        Component numberComp = Component.text(formatted);
        return isPrefix ? symbol.append(Component.space()).append(numberComp) : numberComp.append(Component.space()).append(symbol);
    }

    public Component formatName(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ONE) == 0 ? name : pluralName;
    }
}