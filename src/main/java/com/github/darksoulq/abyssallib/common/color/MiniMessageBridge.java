package com.github.darksoulq.abyssallib.common.color;

import com.github.darksoulq.abyssallib.common.color.gradient.AbstractGradient;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Color;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MiniMessageBridge {
    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
        .hexColors()
        .useUnusualXRepeatedCharacterHexFormat()
        .build();

    public static Component parse(String input, TagResolver... resolvers) {
        return MM.deserialize(input, resolvers);
    }

    public static String toHex(Color color) {
        return String.format("<#%06x>", color.asRGB() & 0xFFFFFF);
    }

    public static String toLegacyHex(Color color) {
        return String.format("&#%06x", color.asRGB() & 0xFFFFFF);
    }

    public static String convertToLegacy(Component component) {
        return LEGACY.serialize(component);
    }

    public static String stripTags(String input) {
        return MM.stripTags(input);
    }

    public static String generateGradientTag(AbstractGradient gradient, String phase) {
        StringBuilder sb = new StringBuilder("<gradient:");
        Color[] colors = gradient.getColors();
        for (int i = 0; i < colors.length; i++) {
            sb.append(String.format("#%06x", colors[i].asRGB() & 0xFFFFFF));
            if (i < colors.length - 1) sb.append(":");
        }
        sb.append(":").append(phase).append(">");
        return sb.toString();
    }

    public static Component render(String text, ColorProvider provider) {
        return render(text, provider, 0.0, 1.0);
    }

    public static Component render(String text, ColorProvider provider, double phase) {
        return render(text, provider, phase, 1.0);
    }

    public static Component render(String text, ColorProvider provider, double phase, double frequency) {
        TextComponent.Builder builder = Component.text();
        char[] chars = text.toCharArray();
        int length = chars.length;

        if (length == 0) return builder.build();

        for (int i = 0; i < length; i++) {
            double t = (double) i / (length - 1);
            double effectivePhase = (t * frequency) + phase;

            effectivePhase = effectivePhase - Math.floor(effectivePhase);

            Color color = provider.get(new Vector(i, 0, 0), effectivePhase);
            builder.append(Component.text(chars[i], TextColor.color(color.asRGB())));
        }
        return builder.build();
    }

    public static Animator animator(String text, ColorProvider provider) {
        return new Animator(text, provider, 0.05);
    }

    public static Animator animator(String text, ColorProvider provider, double speed) {
        return new Animator(text, provider, speed);
    }

    public static String toRainbow(String phase) {
        return "<rainbow:" + phase + ">";
    }

    public static ColorProvider fromMiniMessageGradient(String gradientString) {
        String[] parts = gradientString.split(":");
        List<Color> colors = new ArrayList<>();
        for (String part : parts) {
            try {
                if (part.startsWith("#")) {
                    colors.add(ColorUtils.hex(part));
                } else {
                    Color named = ColorUtils.fromName(part);
                    if (named != null) colors.add(named);
                    else colors.add(Color.WHITE);
                }
            } catch (IllegalArgumentException ignored) {}
        }
        if (colors.isEmpty()) return ColorProvider.fixed(Color.WHITE);
        return ColorProvider.linear(colors);
    }

    public static class Animator {
        private String text;
        private ColorProvider provider;
        private double speed;
        private double phase;
        private double frequency = 1.0;

        public Animator(String text, ColorProvider provider, double speed) {
            this.text = text;
            this.provider = provider;
            this.speed = speed;
            this.phase = 0;
        }

        public Animator setSpeed(double speed) {
            this.speed = speed;
            return this;
        }

        public Animator setFrequency(double frequency) {
            this.frequency = frequency;
            return this;
        }

        public Animator setText(String text) {
            this.text = text;
            return this;
        }

        public Animator setProvider(ColorProvider provider) {
            this.provider = provider;
            return this;
        }

        public Component next() {
            phase += speed;
            if (phase > 1.0) phase -= 1.0;
            return render(text, provider, phase, frequency);
        }

        public Component current() {
            return render(text, provider, phase, frequency);
        }

        public void reset() {
            phase = 0;
        }
    }
}