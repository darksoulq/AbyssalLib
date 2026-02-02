package com.github.darksoulq.abyssallib.server.resource.asset.definition;

import com.github.darksoulq.abyssallib.server.resource.asset.Texture;

import javax.annotation.Nullable;
import java.time.ZoneId;
import java.util.*;

public interface Selector {
    String id();

    Map<String, Object> toJson();

    class Model implements Selector {
        private final com.github.darksoulq.abyssallib.server.resource.asset.Model model;
        private final List<Tint> tints = new LinkedList<>();

        public Model(com.github.darksoulq.abyssallib.server.resource.asset.Model model, Tint... tints) {
            this.model = model;
            this.tints.addAll(List.of(tints));
        }

        @Override
        public String id() {
            return "minecraft:model";
        }

        @Override
        public Map<String, Object> toJson() {
            Map<String, Object> json = new LinkedHashMap<>();
            json.put("type", id());
            json.put("model", model.file());

            List<Map<String, Object>> tintJsons = new LinkedList<>();
            for (Tint tint : tints) {
                tintJsons.add(tint.toJson());
            }
            json.put("tints", tintJsons);
            return json;
        }
    }

    class BundleSelectedItem implements Selector {
        @Override
        public String id() {
            return "minecraft:bundle/selected_item";
        }

        @Override
        public Map<String, Object> toJson() {
            return Map.of("type", id());
        }
    }
    class Select implements Selector {
        private final Property property;
        private Selector fallback = null;

        public Select(Property property) {
            this.property = property;
        }
        public Select(Property property, Selector fallback) {
            this.property = property;
            this.fallback = fallback;
        }

        @Override
        public String id() {
            return "minecraft:select";
        }

        @Override
        public Map<String, Object> toJson() {
            Map<String, Object> json = new LinkedHashMap<>();
            json.put("type", id());
            json.put("property", property.id());
            switch (property) {
                case BlockState blockState -> {
                    json.put("block_state_property", blockState.blockStateProperty);
                    List<Object> cases = new ArrayList<>();
                    blockState.cases.forEach(c -> cases.add(c.toJson()));
                    json.put("cases", cases);
                }
                case CustomModelData customModelData -> {
                    if (customModelData.index != 0) json.put("index", customModelData.index);
                    List<Object> cases = new ArrayList<>();
                    customModelData.cases.forEach(c -> cases.add(c.toJson()));
                    json.put("cases", cases);
                }
                case LocalTime localTime -> {
                    if (localTime.pattern != null) json.put("pattern", localTime.pattern);
                    if (localTime.locale != null) json.put("locale", localTime.locale);
                    if (localTime.zoneId != null) json.put("time_zone", localTime.zoneId);
                    List<Object> cases = new ArrayList<>();
                    localTime.cases.forEach(c -> cases.add(c.toJson()));
                    json.put("cases", cases);
                }
                case Property obj -> {
                    List<Object> cases = new ArrayList<>();
                    obj.getCases().forEach(c -> cases.add(c.toJson()));
                    json.put("cases", cases);
                }
            }
            if (fallback != null) json.put("fallback", fallback.toJson());

            return json;
        }

        public interface Property {
            String id();
            List<Case> getCases();
        }
        public interface Case {
            Map<String, Object> toJson();
        }

        public static class BlockState implements Property {
            private final List<Case> cases = new ArrayList<>();
            private final String blockStateProperty;

            public BlockState(List<Entry> cases, String blockStateProperty) {
                this.cases.addAll(cases);
                this.blockStateProperty = blockStateProperty;
            }

            @Override
            public String id() {
                return "minecraft:block_state";
            }
            @Override
            public List<Case> getCases() {
                return cases;
            }

            public static class Entry implements Case {
                private final List<String> when = new ArrayList<>();
                private final Selector model;

                public Entry(String when, Selector model) {
                    this.when.add(when);
                    this.model = model;
                }
                public Entry(List<String> when, Selector model) {
                    this.when.addAll(when);
                    this.model = model;
                }

                @Override
                public Map<String, Object> toJson() {
                    Map<String, Object> json = new LinkedHashMap<>();
                    json.put("when", when.size() == 1 ? when.getFirst() : when);
                    json.put("model", model.toJson());
                    return json;
                }
            }
        }
        public static class CustomModelData implements Property {
            private final int index;
            private final List<Case> cases = new ArrayList<>();

            public CustomModelData(List<Case> cases) {
                this.index = 0;
                this.cases.addAll(cases);
            }
            public CustomModelData(int index, List<Case> cases) {
                this.index = index;
                this.cases.addAll(cases);
            }

            @Override
            public String id() {
                return "minecraft:custom_model_data";
            }

            public int getIndex() {
                return index;
            }
            @Override
            public List<Case> getCases() {
                return cases;
            }

            public static class Entry implements Case {
                private final List<String> cases = new ArrayList<>();
                private final Selector model;

                public Entry(String cases, Selector model) {
                    this.cases.add(cases);
                    this.model = model;
                }

                public Entry(List<String> cases, Selector model) {
                    this.cases.addAll(cases);
                    this.model = model;
                }

                public List<String> getCases() {
                    return cases;
                }
                public Selector getModel() {
                    return model;
                }

                @Override
                public Map<String, Object> toJson() {
                    Map<String, Object> json = new LinkedHashMap<>();
                    json.put("when", cases.size() == 1 ? cases.getFirst() : cases);
                    json.put("model", model.toJson());
                    return json;
                }
            }
        }
        public static class ChargeType implements Property {
            private final List<Case> cases = new ArrayList<>();

            public ChargeType(List<Entry> when) {
                this.cases.addAll(when);
            }

            @Override
            public String id() {
                return "minecraft:charge_type";
            }
            @Override
            public List<Case> getCases() {
                return cases;
            }

            public static class Entry implements Case {
                private final List<Type> when = new ArrayList<>();
                private final Selector model;

                public Entry(Type type, Selector model) {
                    this.when.add(type);
                    this.model = model;
                }
                public Entry(List<Type> types, Selector model) {
                    this.when.addAll(types);
                    this.model = model;
                }

                @Override
                public Map<String, Object> toJson() {
                    Map<String, Object> json = new LinkedHashMap<>();
                    json.put("when", when.size() == 1 ? when.getFirst() : when);
                    json.put("model", model.toJson());
                    return json;
                }
            }
            public enum Type {
                NONE, ARROW, ROCKET
            }
        }
        public static class ContextDimension implements Property {
            private final List<Case> cases = new ArrayList<>();

            public ContextDimension(List<Entry> cases) {
                this.cases.addAll(cases);
            }

            @Override
            public String id() {
                return "minecraft:context_dimension";
            }

            @Override
            public List<Case> getCases() {
                return cases;
            }

            public static class Entry implements Case {
                private final List<String> when = new ArrayList<>();
                private final Selector model;

                public Entry(String when, Selector model) {
                    this.when.add(when);
                    this.model = model;
                }
                public Entry(List<String> when, Selector model) {
                    this.when.addAll(when);
                    this.model = model;
                }

                @Override
                public Map<String, Object> toJson() {
                    Map<String, Object> json = new LinkedHashMap<>();
                    json.put("when", when.size() == 1 ? when.getFirst() : when);
                    json.put("model", model.toJson());
                    return json;
                }
            }
        }
        public static class ContextEntityType implements Property {
            private final List<Case> cases = new ArrayList<>();

            public ContextEntityType(List<Entry> cases) {
                this.cases.addAll(cases);
            }

            @Override
            public String id() {
                return "minecraft:context_entity_type";
            }

            @Override
            public List<Case> getCases() {
                return cases;
            }

            public static class Entry implements Case {
                private final List<String> when = new ArrayList<>();
                private final Selector model;

                public Entry(String when, Selector model) {
                    this.when.add(when);
                    this.model = model;
                }
                public Entry(List<String> when, Selector model) {
                    this.when.addAll(when);
                    this.model = model;
                }

                @Override
                public Map<String, Object> toJson() {
                    Map<String, Object> json = new LinkedHashMap<>();
                    json.put("when", when.size() == 1 ? when.getFirst() : when);
                    json.put("model", model.toJson());
                    return json;
                }
            }
        }
        public static class DisplayContext implements Property {
            private final List<Case> cases = new ArrayList<>();

            public DisplayContext(List<Entry> cases) {
                this.cases.addAll(cases);
            }

            @Override
            public String id() {
                return "minecraft:display_context";
            }
            public List<Case> getCases() {
                return cases;
            }

            public static class Entry implements Case {
                private final List<Display> when = new ArrayList<>();
                private final Selector model;

                public Entry(Display when, Selector model) {
                    this.when.add(when);
                    this.model = model;
                }
                public Entry(List<Display> when, Selector model) {
                    this.when.addAll(when);
                    this.model = model;
                }

                @Override
                public Map<String, Object> toJson() {
                    Map<String, Object> json = new LinkedHashMap<>();
                    json.put("when", when.size() == 1 ? when.getFirst().name().toLowerCase() : when.stream().map(w -> w.name().toLowerCase()));
                    json.put("model", model.toJson());
                    return json;
                }
            }
            public enum Display {
                FIRSTPERSON_RIGHTHAND, FIRSTPERSON_LEFTHAND,
                THIRDPERSON_RIGHTHAND, THIRDPERSON_LEFTHAND,
                GUI, HEAD, GROUND, FIXED, ON_SHELF, NONE
            }
        }
        public static class LocalTime implements Property {
            private final List<Case> cases = new ArrayList<>();
            private String pattern = null;
            private String locale = null;
            private String zoneId = null;

            public LocalTime(List<Entry> cases) {
                this.cases.addAll(cases);
            }

            public LocalTime(List<Entry> cases, String timeFormat) {
                this.cases.addAll(cases);
                this.pattern = timeFormat;
            }

            public LocalTime(List<Entry> cases, Locale locale) {
                this.cases.addAll(cases);
                this.locale = locale.toLanguageTag();
            }

            public LocalTime(List<Entry> cases, ZoneId zoneId) {
                this.cases.addAll(cases);
                this.zoneId = zoneId.getId();
            }

            public LocalTime(List<Entry> cases, String timeFormat, Locale locale) {
                this.cases.addAll(cases);
                this.pattern = timeFormat;
                this.locale = locale.toLanguageTag();
            }

            public LocalTime(List<Entry> cases, String timeFormat, ZoneId zoneId) {
                this.cases.addAll(cases);
                this.pattern = timeFormat;
                this.zoneId = zoneId.getId();
            }

            public LocalTime(List<Entry> cases, Locale locale, ZoneId zoneId) {
                this.cases.addAll(cases);
                this.locale = locale.toLanguageTag();
                this.zoneId = zoneId.getId();
            }

            public LocalTime(List<Entry> cases, String timeFormat, Locale locale, ZoneId zoneId) {
                this.cases.addAll(cases);
                this.pattern = timeFormat;
                this.locale = locale.toLanguageTag();
                this.zoneId = zoneId.getId();
            }

            public String getPattern() {
                return pattern;
            }
            public String getLocale() {
                return locale;
            }
            public String getZoneId() {
                return zoneId;
            }

            @Override
            public String id() {
                return "minecraft:local_time";
            }
            @Override
            public List<Case> getCases() {
                return cases;
            }

            public static class Entry implements Case {
                private final List<String> when = new ArrayList<>();
                private final Selector model;

                public Entry(String when, Selector model) {
                    this.when.add(when);
                    this.model = model;
                }
                public Entry(List<String> when, Selector model) {
                    this.when.addAll(when);
                    this.model = model;
                }

                @Override
                public Map<String, Object> toJson() {
                    Map<String, Object> json = new LinkedHashMap<>();
                    json.put("when", when.size() == 1 ? when.getFirst() : when);
                    json.put("model", model.toJson());
                    return json;
                }
            }
        }
        public static class MainHand implements Property {
            private final List<Case> cases = new ArrayList<>();

            public MainHand(List<Entry> cases) {
                this.cases.addAll(cases);
            }

            @Override
            public String id() {
                return "minecraft:main_hand";
            }
            @Override
            public List<Case> getCases() {
                return cases;
            }

            public static class Entry implements Case {
                private final List<Hand> when = new ArrayList<>();
                private final Selector model;

                public Entry(Hand when, Selector model) {
                    this.when.add(when);
                    this.model = model;
                }
                public Entry(List<Hand> when, Selector model) {
                    this.when.addAll(when);
                    this.model = model;
                }

                @Override
                public Map<String, Object> toJson() {
                    Map<String, Object> json = new LinkedHashMap<>();
                    json.put("when", when.size() == 1 ? when.getFirst().name().toLowerCase() : when.stream().map(w -> w.name().toLowerCase()));
                    json.put("model", model.toJson());
                    return json;
                }
            }
            public enum Hand {
                LEFT, RIGHT
            }
        }
        public static class TrimMaterial implements Property {
            private final List<Case> cases = new ArrayList<>();

            public TrimMaterial(List<Entry> cases) {
                this.cases.addAll(cases);
            }

            @Override
            public String id() {
                return "minecraft:trim_material";
            }
            @Override
            public List<Case> getCases() {
                return cases;
            }

            public static class Entry implements Case {
                private final List<String> when = new ArrayList<>();
                private final Selector model;

                public Entry(String when, Selector model) {
                    this.when.add(when);
                    this.model = model;
                }
                public Entry(List<String> when, Selector model) {
                    this.when.addAll(when);
                    this.model = model;
                }

                @Override
                public Map<String, Object> toJson() {
                    Map<String, Object> json = new LinkedHashMap<>();
                    json.put("when", when.size() == 1 ? when.getFirst() : when);
                    json.put("model", model.toJson());
                    return json;
                }
            }
        }
    }
    class Condition implements Selector {
        private final Property property;
        private final Selector onTrue;
        private final Selector onFalse;

        public Condition(Property property, Selector onTrue, Selector onFalse) {
            this.property = property;
            this.onTrue = onTrue;
            this.onFalse = onFalse;
        }

        @Override
        public String id() {
            return "minecraft:condition";
        }

        @Override
        public Map<String, Object> toJson() {
            Map<String, Object> json = new LinkedHashMap<>();
            json.put("type", id());
            json.put("property", property.id());
            switch (property) {
                case CustomModelData customModelData -> json.put("index", customModelData.index);
                case Component component -> {
                    json.put("predicate", component.component.id());
                    json.put("value", component.component.toJson());
                }
                case KeybindDown keybindDown -> json.put("keybind", keybindDown.keybind.type);
                case HasComponent hasComponent -> {
                    json.put("component", hasComponent.component.id());
                    json.put("ignore_default", hasComponent.ignoreDefault);
                }
                default -> {}
            }
            json.put("on_true", onTrue.toJson());
            json.put("on_false", onFalse.toJson());
            return json;
        }

        public interface Property {
            String id();
        }

        public static class Broken implements Property {
            @Override
            public String id() {
                return "minecraft:broken";
            }
        }
        public static class BundleHasSelectedItem implements Property {
            @Override
            public String id() {
                return "minecraft:bundle/has_selected_item";
            }
        }
        public static class Carried implements Property {
            @Override
            public String id() {
                return "minecraft:carried";
            }
        }
        public static class CustomModelData implements Property {
            private final int index;

            public CustomModelData(int index) {
                this.index = index;
            }

            @Override
            public String id() {
                return "minecraft:custom_model_data";
            }
        }
        public static class Component implements Property {
            private final ValueComponent component;

            public Component(ValueComponent component) {
                this.component = component;
            }

            @Override
            public String id() {
                return "minecraft:component";
            }
        }
        public static class Damaged implements Property {
            @Override
            public String id() {
                return "minecraft:damaged";
            }
        }
        public static class ExtendedView implements Property {
            @Override
            public String id() {
                return "minecraft:extended_view";
            }
        }
        public static class FishingRodCast implements Property {
            @Override
            public String id() {
                return "minecraft:fishing_rod/cast";
            }
        }
        public static class HasComponent implements Property {
            private final ContainedComponent component;
            private final boolean ignoreDefault;

            public HasComponent(ContainedComponent component, boolean ignoreDefault) {
                this.component = component;
                this.ignoreDefault = ignoreDefault;
            }

            @Override
            public String id() {
                return "minecraft:has_component";
            }
        }
        public static class KeybindDown implements Property {
            private final Keybind keybind;

            public KeybindDown(Keybind keybind) {
                this.keybind = keybind;
            }

            @Override
            public String id() {
                return "minecraft:keybind_down";
            }

            public enum Keybind {
                ADVANCEMENTS("key.advancements"),
                ATTACK("key.attack"),
                BACK("key.back"),
                CHAT("key.chat"),
                COMMAND("key.command"),
                DROP("key.drop"),
                FORWARD("key.forward"),
                FULLSCREEN("key.fullscreen"),
                HOTBAR_1("key.hotbar.1"),
                HOTBAR_2("key.hotbar.2"),
                HOTBAR_3("key.hotbar.3"),
                HOTBAR_4("key.hotbar.4"),
                HOTBAR_5("key.hotbar.5"),
                HOTBAR_6("key.hotbar.6"),
                HOTBAR_7("key.hotbar.7"),
                HOTBAR_8("key.hotbar.8"),
                HOTBAR_9("key.hotbar.9"),
                INVENTORY("key.inventory"),
                JUMP("key.jump"),
                LEFT("key.left"),
                LOAD_TOOLBAR_ACTIVATOR("key.loadToolbarActivator"),
                SCREENSHOT("key.screenshot"),
                SMOOTH_CAMERA("key.smoothCamera"),
                SNEAK("key.sneak"),
                SPECTATOR_OUTLINES("key.spectatorOutlines"),
                SPRINT("key.sprint"),
                SWAP_OFFHAND("key.swapOffhand"),
                TOGGLE_PERSPECTIVE("key.togglePerspective"),
                USE("key.use");

                public final String type;

                Keybind(String type) {
                    this.type = type;
                }
            }
        }
        public static class Selected implements Property {
            @Override
            public String id() {
                return "minecraft:selected";
            }
        }
        public static class UsingItem implements Property {
            @Override
            public String id() {
                return "minecraft:using_item";
            }
        }
        public static class ViewEntity implements Property {
            @Override
            public String id() {
                return "minecraft:view_entity";
            }
        }
    }
    class Composite implements Selector {
        private final List<Selector> selectors = new LinkedList<>();

        public Composite add(Selector selector) {
            selectors.add(selector);
            return this;
        }

        @Override
        public String id() {
            return "minecraft:composite";
        }

        @Override
        public Map<String, Object> toJson() {
            Map<String, Object> json = new LinkedHashMap<>();
            json.put("type", id());
            List<Map<String, Object>> selectorJsons = new LinkedList<>();

            for (Selector sel : selectors) {
                selectorJsons.add(sel.toJson());
            }
            json.put("models", selectorJsons);
            return json;
        }
    }
    class Empty implements Selector {
        @Override
        public String id() {
            return "minecraft:empty";
        }

        @Override
        public Map<String, Object> toJson() {
            return Map.of("type", id());
        }
    }
    class RangeDispatch implements Selector {
        private final Property property;
        private final List<Entry> entries = new LinkedList<>();
        private int scale = 0;
        private Selector fallback = null;

        public RangeDispatch(Property property) {
            this.property = property;
        }
        public RangeDispatch(Property property, int scale) {
            this.property = property;
            this.scale = scale;
        }
        public RangeDispatch entry(int threshold, Selector selector) {
            entries.add(new Entry(threshold, selector));
            return this;
        }
        public RangeDispatch fallback(Selector selector) {
            this.fallback = selector;
            return this;
        }

        @Override
        public String id() {
            return "minecraft:range_dispatch";
        }

        @Override
        public Map<String, Object> toJson() {
            Map<String, Object> json = new LinkedHashMap<>();
            json.put("type", id());
            json.put("property", property.id());
            switch (property) {
                case Compass compassProperty -> {
                    json.put("target", compassProperty.target.type);
                    json.put("wobble", compassProperty.wobble);
                }
                case Time timeProperty -> {
                    json.put("target", timeProperty.target.type);
                    json.put("wobble", timeProperty.wobble);
                }
                case UseCycle useCycleProperty -> json.put("period", useCycleProperty.period);
                case UseDuration useDurationProperty -> json.put("remaining", useDurationProperty.remaining);
                case CustomModelData customModelDataProperty -> json.put("index", customModelDataProperty.index);
                case Damage damageProperty -> json.put("normalize", damageProperty.normalize);
                case Count countProperty -> json.put("normalize", countProperty.normalize);
                default -> {
                }
            }
            json.put("scale", scale);
            List<Map<String, Object>> entryJsons = new LinkedList<>();

            for (Entry entry : entries) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("threshold", entry.threshold);
                map.put("model", entry.selector.toJson());
                entryJsons.add(map);
            }
            json.put("entries", entryJsons);
            json.put("fallback", fallback.toJson());
            return json;
        }

        public interface Property {
            String id();
        }

        public record Entry(
                int threshold,
                Selector selector
        ) {}

        public static class BundleFullness implements Property {
            @Override
            public String id() {
                return "minecraft:bundle_fullness";
            }
        }
        public static class Cooldown implements Property {
            @Override
            public String id() {
                return "minecraft:cooldown";
            }
        }
        public static class CrossbowPull implements Property {
            @Override
            public String id() {
                return "minecraft:crossbow_pull";
            }
        }
        public record Count(boolean normalize) implements Property {

            @Override
            public String id() {
                return "minecraft:count";
            }
        }
        public record Damage(boolean normalize) implements Property {
            @Override
            public String id() {
                return "minecraft:damage";
            }
        }
        public record UseDuration(boolean remaining) implements Property {

            @Override
            public String id() {
                return "minecraft:use_duration";
            }
        }
        public record CustomModelData(int index) implements Property {

            @Override
            public String id() {
                return "minecraft:custom_model_data";
            }
        }
        public record UseCycle(int period) implements Property {

            @Override
            public String id() {
                return "minecraft:use_cycle";
            }
        }
        public record Compass(RangeDispatch.Compass.Target target, boolean wobble) implements Property {

            @Override
            public String id() {
                return "minecraft:compass";
            }

            public enum Target {
                SPAWN("spawn"),
                LODESTONE("lodestone"),
                RECOVERY("recovery");

                final String type;

                Target(String type) {
                    this.type = type;
                }
            }
        }
        public record Time(RangeDispatch.Time.Target target, boolean wobble) implements Property {

            @Override
            public String id() {
                return "minecraft:time";
            }

            public enum Target {
                DAY_TIME("daytime"),
                MOON_PHASE("moon_phase"),
                RANDOM("random");

                final String type;

                Target(String type) {
                    this.type = type;
                }
            }
        }
    }
    class Special implements Selector {
        private final com.github.darksoulq.abyssallib.server.resource.asset.Model base;
        private final Type type;

        public Special(com.github.darksoulq.abyssallib.server.resource.asset.Model base, Type type) {
            this.base = base;
            this.type = type;
        }

        @Override
        public String id() {
            return "minecraft:special";
        }

        @Override
        public Map<String, Object> toJson() {
            Map<String, Object> json = new LinkedHashMap<>();
            json.put("type", id());
            json.put("model", type.toJson());
            json.put("base", base.file());
            return json;
        }

        public interface Type {
            String id();

            Map<String, Object> toJson();
        }

        public static class Banner implements Type {
            private final NamedColor color;

            public Banner(NamedColor color) {
                this.color = color;
            }

            @Override
            public String id() {
                return "minecraft:banner";
            }

            @Override
            public Map<String, Object> toJson() {
                Map<String, Object> json = new LinkedHashMap<>();
                json.put("type", id());
                json.put("color", color.name().toLowerCase());
                return json;
            }
        }

        public static class Bed implements Type {
            private final Texture texture;

            public Bed(Texture texture) {
                this.texture = texture;
            }

            @Override
            public String id() {
                return "minecraft:bed";
            }

            @Override
            public Map<String, Object> toJson() {
                Map<String, Object> json = new LinkedHashMap<>();
                json.put("type", id());
                json.put("texture", texture.file());
                return json;
            }
        }

        public static class Conduit implements Type {
            @Override
            public String id() {
                return "minecraft:conduit";
            }

            @Override
            public Map<String, Object> toJson() {
                return Map.of("type", id());
            }
        }

        public static class Chest implements Type {
            private final Texture texture;
            private final int openness;

            public Chest(Texture texture, int openness) {
                this.texture = texture;
                this.openness = openness;
            }

            @Override
            public String id() {
                return "minecraft:chest";
            }

            @Override
            public Map<String, Object> toJson() {
                Map<String, Object> json = new LinkedHashMap<>();
                json.put("type", id());
                json.put("texture", texture.file());
                json.put("openness", openness);
                return json;
            }
        }

        public static class DecoratedPot implements Type {
            @Override
            public String id() {
                return "minecraft:decorated_pot";
            }

            @Override
            public Map<String, Object> toJson() {
                return Map.of("type", id());
            }
        }

        public static class HangingSign implements Type {
            private final WoodType type;
            private final Texture texture;

            public HangingSign(WoodType type, Texture texture) {
                this.type = type;
                this.texture = texture;
            }

            @Override
            public String id() {
                return "minecraft:hanging_sign";
            }

            @Override
            public Map<String, Object> toJson() {
                Map<String, Object> json = new LinkedHashMap<>();
                json.put("type", id());
                json.put("wood_type", type.name().toLowerCase());
                json.put("texture", texture.file());
                return json;
            }
        }

        public static class Head implements Type {
            private final HeadType kind;
            private final Texture texture;
            private final int animationTime;

            public Head(HeadType kind, @Nullable Texture texture, int animationTime) {
                this.kind = kind;
                this.texture = texture;
                this.animationTime = animationTime;
            }

            @Override
            public String id() {
                return "minecraft:head";
            }

            @Override
            public Map<String, Object> toJson() {
                Map<String, Object> json = new LinkedHashMap<>();
                json.put("type", id());
                json.put("kind", kind.name().toLowerCase());
                if (texture != null) {
                    json.put("texture", texture.file());
                }
                if (kind.equals(HeadType.DRAGON) || kind.equals(HeadType.PIGLIN)) {
                    json.put("animation", animationTime);
                }
                return json;
            }
        }

        public static class Shield implements Type {
            @Override
            public String id() {
                return "minecraft:shield";
            }

            @Override
            public Map<String, Object> toJson() {
                return Map.of("type", id());
            }
        }

        public static class ShulkerBox implements Type {
            private final Texture texture;
            private final int openness;
            private final Orientation orientaion;

            public ShulkerBox(Texture texture, int openness, Orientation orientaion) {
                this.texture = texture;
                this.openness = openness;
                this.orientaion = orientaion;
            }

            @Override
            public String id() {
                return "minecraft:shulker_box";
            }

            @Override
            public Map<String, Object> toJson() {
                Map<String, Object> json = new LinkedHashMap<>();
                json.put("type", id());
                json.put("texture", texture.file());
                json.put("openness", openness);
                json.put("orientation", orientaion.name().toLowerCase());
                return json;
            }
        }

        public static class StandingSign implements Type {
            private final WoodType type;
            private final Texture texture;

            public StandingSign(WoodType type, Texture texture) {
                this.type = type;
                this.texture = texture;
            }

            @Override
            public String id() {
                return "minecraft:standing_sign";
            }

            @Override
            public Map<String, Object> toJson() {
                Map<String, Object> json = new LinkedHashMap<>();
                json.put("type", id());
                json.put("wood_type", type.name().toLowerCase());
                json.put("texture", texture.file());
                return json;
            }
        }

        public static class Trident implements Type {
            @Override
            public String id() {
                return "minecraft:trident";
            }

            @Override
            public Map<String, Object> toJson() {
                return Map.of("type", id());
            }
        }
    }
}
