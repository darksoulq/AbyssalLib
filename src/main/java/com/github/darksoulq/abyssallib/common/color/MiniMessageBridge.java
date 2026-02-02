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

/**
 * A bridge utility connecting the internal color systems with Adventure's MiniMessage.
 * <p>
 * Provides functionality for parsing MiniMessage strings, converting colors to
 * various hex formats, and rendering dynamic, animated text components using
 * {@link ColorProvider}.
 */
public class MiniMessageBridge {
    /** Internal instance of the MiniMessage parser. */
    private static final MiniMessage MM = MiniMessage.miniMessage();

    /** Serializer for converting Adventure Components to legacy Bukkit strings with Hex support. */
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
        .hexColors()
        .useUnusualXRepeatedCharacterHexFormat()
        .build();

    /**
     * Deserializes a MiniMessage string into an Adventure {@link Component}.
     *
     * @param input     The string containing MiniMessage tags.
     * @param resolvers Optional {@link TagResolver}s for custom placeholder handling.
     * @return The parsed {@link Component}.
     */
    public static Component parse(String input, TagResolver... resolvers) {
        return MM.deserialize(input, resolvers);
    }

    /**
     * Converts a Bukkit Color into a MiniMessage-compatible hex tag.
     *
     * @param color The {@link Color} to format.
     * @return A string formatted as {@code <#RRGGBB>}.
     */
    public static String toHex(Color color) {
        return String.format("<#%06x>", color.asRGB() & 0xFFFFFF);
    }

    /**
     * Converts a Bukkit Color into a legacy Bukkit hex code.
     *
     * @param color The {@link Color} to format.
     * @return A string formatted as {@code &#RRGGBB}.
     */
    public static String toLegacyHex(Color color) {
        return String.format("&#%06x", color.asRGB() & 0xFFFFFF);
    }

    /**
     * Serializes an Adventure Component into a legacy Bukkit string.
     *
     * @param component The {@link Component} to serialize.
     * @return A legacy-formatted string with color codes.
     */
    public static String convertToLegacy(Component component) {
        return LEGACY.serialize(component);
    }

    /**
     * Strips all MiniMessage tags from an input string, leaving only plain text.
     *
     * @param input The tagged string.
     * @return The cleaned plain text.
     */
    public static String stripTags(String input) {
        return MM.stripTags(input);
    }

    /**
     * Generates a MiniMessage gradient tag from an {@link AbstractGradient}.
     *
     * @param gradient The source {@link AbstractGradient}.
     * @param phase    An optional phase/offset parameter for the gradient.
     * @return A formatted {@code <gradient:#RRGGBB:#RRGGBB...:phase>} tag.
     */
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

    /**
     * Renders a string with colors provided by a {@link ColorProvider} per character.
     *
     * @param text     The text to render.
     * @param provider The {@link ColorProvider} logic to apply.
     * @return A {@link Component} with procedural coloring.
     */
    public static Component render(String text, ColorProvider provider) {
        return render(text, provider, 0.0, 1.0);
    }

    /**
     * Renders a string with procedural colors and a specific starting phase.
     *
     * @param text     The text to render.
     * @param provider The {@link ColorProvider}.
     * @param phase    The temporal offset (0.0 to 1.0).
     * @return A {@link Component} with procedural coloring.
     */
    public static Component render(String text, ColorProvider provider, double phase) {
        return render(text, provider, phase, 1.0);
    }

    /**
     * Renders a string with procedural colors, phase, and frequency modulation.
     *
     * @param text      The text to render.
     * @param provider  The {@link ColorProvider}.
     * @param phase     The temporal offset (0.0 to 1.0).
     * @param frequency The spatial frequency (how fast the colors change across the string).
     * @return A {@link Component} with procedural coloring.
     */
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

    /**
     * Creates an animator for the specified text and provider.
     *
     * @param text     The text to animate.
     * @param provider The {@link ColorProvider} providing colors.
     * @return A new {@link Animator} instance.
     */
    public static Animator animator(String text, ColorProvider provider) {
        return new Animator(text, provider, 0.05);
    }

    /**
     * Creates an animator with a custom animation speed.
     *
     * @param text     The text to animate.
     * @param provider The {@link ColorProvider}.
     * @param speed    The phase increment per frame.
     * @return A new {@link Animator} instance.
     */
    public static Animator animator(String text, ColorProvider provider, double speed) {
        return new Animator(text, provider, speed);
    }

    /**
     * Returns a MiniMessage rainbow tag.
     *
     * @param phase The phase/offset for the rainbow.
     * @return A {@code <rainbow:phase>} tag.
     */
    public static String toRainbow(String phase) {
        return "<rainbow:" + phase + ">";
    }

    /**
     * Parses a MiniMessage gradient-style string into an internal {@link ColorProvider}.
     *
     * @param gradientString A colon-separated string of colors (e.g., "#FF0000:blue:white").
     * @return A {@link ColorProvider} representing the linear gradient defined by the string.
     */
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

    /**
     * A utility class for handling stateful text color animations.
     */
    public static class Animator {
        /** The text content being animated. */
        private String text;
        /** The color source for the animation. */
        private ColorProvider provider;
        /** The rate at which the phase advances. */
        private double speed;
        /** The current temporal phase (0.0 to 1.0). */
        private double phase;
        /** The spatial frequency across the text. */
        private double frequency = 1.0;

        /**
         * @param text     The text.
         * @param provider The color source.
         * @param speed    The increment speed.
         */
        public Animator(String text, ColorProvider provider, double speed) {
            this.text = text;
            this.provider = provider;
            this.speed = speed;
            this.phase = 0;
        }

        /** @param speed The new phase increment value. @return This animator. */
        public Animator setSpeed(double speed) {
            this.speed = speed;
            return this;
        }

        /** @param frequency The new spatial frequency. @return This animator. */
        public Animator setFrequency(double frequency) {
            this.frequency = frequency;
            return this;
        }

        /** @param text The new string content. @return This animator. */
        public Animator setText(String text) {
            this.text = text;
            return this;
        }

        /** @param provider The new color source. @return This animator. */
        public Animator setProvider(ColorProvider provider) {
            this.provider = provider;
            return this;
        }

        /**
         * Advances the animation phase by {@link #speed} and returns the rendered component.
         *
         * @return The next frame of the animation as a {@link Component}.
         */
        public Component next() {
            phase += speed;
            if (phase > 1.0) phase -= 1.0;
            return render(text, provider, phase, frequency);
        }

        /**
         * Returns the current frame of the animation without advancing the phase.
         *
         * @return The current {@link Component}.
         */
        public Component current() {
            return render(text, provider, phase, frequency);
        }

        /**
         * Resets the animation phase back to zero.
         */
        public void reset() {
            phase = 0;
        }
    }
}