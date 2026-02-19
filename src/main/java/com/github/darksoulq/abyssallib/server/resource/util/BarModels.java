package com.github.darksoulq.abyssallib.server.resource.util;

import com.github.darksoulq.abyssallib.server.resource.Namespace;
import com.github.darksoulq.abyssallib.server.resource.asset.Model;
import com.github.darksoulq.abyssallib.server.resource.asset.definition.Selector;

import java.util.*;

/**
 * A utility class designed to programmatically generate complex, layered item models
 * for progress bars, health bars, and mana bars within a Minecraft resource pack.
 * <p>
 * This class automates the creation of {@link Selector} trees based on Custom Model Data,
 * allowing for dynamic textures that change based on progress states and custom string keys.
 */
public class BarModels {

    /**
     * Represents the primary axis of the bar's orientation and fill direction.
     */
    public enum Axis {
        /**
         * Represents a bar that is oriented horizontally.
         */
        HORIZONTAL,
        /**
         * Represents a bar that is oriented vertically.
         */
        VERTICAL
    }

    /**
     * A fluent builder class used to configure and register bar models into a {@link Namespace}.
     */
    public static class Builder {
        /** The namespace for asset registration. */
        private final Namespace ns;
        /** The base identifier for the bar asset. */
        private final String name;
        /** The visual axis of the bar. */
        private final Axis axis;
        /** The number of discrete visual increments. */
        private int steps = 8;
        /** Whether to automatically register .mcmeta files for fill layers. */
        private boolean animatedFill = false;

        /** The fallback branch for rendering. */
        private Branch defaultBranch;
        /** Conditional branches for state-based rendering. */
        private final Map<List<String>, Branch> branches = new LinkedHashMap<>();
        /** Cache for reusable model selectors. */
        private final Map<String, Selector.Model> modelCache = new HashMap<>();

        /**
         * Constructs a new Builder for a specific bar.
         *
         * @param ns   The namespace to use for registration.
         * @param name The unique name of the bar.
         * @param axis The orientation of the bar.
         */
        public Builder(Namespace ns, String name, Axis axis) {
            this.ns = ns;
            this.name = name;
            this.axis = axis;
        }

        /**
         * Configures the number of progress increments.
         *
         * @param steps The total steps (e.g., 8 for a half-block bar).
         * @return This builder instance.
         */
        public Builder steps(int steps) {
            this.steps = steps;
            return this;
        }

        /**
         * Sets whether the fill textures should attempt to load animation metadata.
         *
         * @param animatedFill True to call ns.mcmeta for fill textures.
         * @return This builder instance.
         */
        public Builder animatedFill(boolean animatedFill) {
            this.animatedFill = animatedFill;
            return this;
        }

        /**
         * Sets the default texture configuration for this bar.
         *
         * @param underlay The texture path for the background layer.
         * @param fill     The base texture path for the fill segments.
         * @param overlay  The texture path for the foreground/border layer.
         * @return This builder instance.
         */
        public Builder defaultBranch(String underlay, String fill, String overlay) {
            this.defaultBranch = new Branch(underlay, fill, overlay);
            return this;
        }

        /**
         * Registers a conditional branch for specific metadata states.
         *
         * @param conditions The list of Custom Model Data strings required.
         * @param underlay   The background texture for this branch.
         * @param fill       The fill base path for this branch.
         * @param overlay    The foreground texture for this branch.
         * @return This builder instance.
         */
        public Builder branch(List<String> conditions, String underlay, String fill, String overlay) {
            this.branches.put(new ArrayList<>(conditions), new Branch(underlay, fill, overlay));
            return this;
        }

        /**
         * Executes the registration of all generated models and item definitions.
         */
        public void register() {
            Node root = new Node();

            if (defaultBranch != null) {
                root.leaf = buildProgressTree(defaultBranch);
            }

            for (Map.Entry<List<String>, Branch> entry : branches.entrySet()) {
                Node current = root;
                for (String cond : entry.getKey()) {
                    current = current.children.computeIfAbsent(cond, k -> new Node());
                }
                current.leaf = buildProgressTree(entry.getValue());
            }

            ns.itemDefinition("bar/" + name, root.toSelector(), false, true, 1.0);
        }

        /**
         * Builds the selector logic for progress states within a branch.
         *
         * @param branch The branch configuration to process.
         * @return A selector representing all progress states for this branch.
         */
        private Selector buildProgressTree(Branch branch) {
            Selector underlay = model(branch.underlay, branch.underlay, axis);
            Selector overlay = model(branch.overlay, branch.overlay, axis);
            Selector[][] fillParts = generateFillParts(branch.fillBase);

            Selector.Composite[] states = new Selector.Composite[steps + 1];

            for (int i = 0; i <= steps; i++) {
                Selector.Composite composite = new Selector.Composite().add(underlay);
                for (int p = 1; p <= i; p++) {
                    composite.add(fillParts[p][p - 1]);
                }
                composite.add(overlay);
                states[i] = composite;
            }

            List<Selector.Select.Case> cases = new ArrayList<>();
            for (int i = 0; i <= steps; i++) {
                cases.add(new Selector.Select.CustomModelData.Entry("bar_progress=" + i, states[i]));
            }

            return new Selector.Select(new Selector.Select.CustomModelData(cases), states[0]);
        }

        /**
         * Generates the multi-dimensional array of fill part selectors.
         *
         * @param fillBasePath The prefix path for fill textures.
         * @return A grid of selectors for individual fill increments.
         */
        private Selector[][] generateFillParts(String fillBasePath) {
            Selector[][] parts = new Selector[steps + 1][];

            for (int i = 1; i <= steps; i++) {
                Selector[] arr = new Selector[i];
                for (int p = 1; p <= i; p++) {
                    String path = fillBasePath + "_" + p;
                    arr[p - 1] = model(path, path, axis);
                    if (animatedFill) {
                        ns.mcmeta(path, true);
                    }
                }
                parts[i] = arr;
            }
            return parts;
        }

        /**
         * Creates or retrieves a model selector for a specific path.
         *
         * @param path      The texture path.
         * @param modelPath The target path for the model file.
         * @param type      The axis of the bar.
         * @return A model selector.
         */
        private Selector.Model model(String path, String modelPath, Axis type) {
            return modelCache.computeIfAbsent(modelPath, p -> {
                Model model = ns.model(modelPath, false);
                model.guiLight(Model.GuiLight.FRONT);
                model.texture("0", ns.texture(path));

                if (type == Axis.HORIZONTAL) {
                    guiItemModelHorizontal(model);
                } else {
                    guiItemModelVertical(model);
                }

                model.display("gui", new Model.Display().translation(0, -0.9574f, 0));
                model.display("fixed", new Model.Display().translation(0, 0, -0.25f));
                return new Selector.Model(model);
            });
        }
    }

    /**
     * Internal container for texture configuration of a single bar style.
     */
    private static class Branch {
        /** Background texture. */
        String underlay;
        /** Incremental fill texture base. */
        String fillBase;
        /** Foreground texture. */
        String overlay;

        /**
         * Constructs a branch.
         *
         * @param underlay Background.
         * @param fillBase Fill base.
         * @param overlay  Foreground.
         */
        Branch(String underlay, String fillBase, String overlay) {
            this.underlay = underlay;
            this.fillBase = fillBase;
            this.overlay = overlay;
        }
    }

    /**
     * Internal node for building nested selector logic trees.
     */
    private static class Node {
        /** Map of child nodes based on metadata strings. */
        Map<String, Node> children = new LinkedHashMap<>();
        /** The leaf selector at this node. */
        Selector leaf;

        /**
         * Recursively converts the node structure into a Selector.
         *
         * @return The finalized selector logic.
         */
        Selector toSelector() {
            if (children.isEmpty()) {
                return leaf != null ? leaf : new Selector.Empty();
            }

            List<Selector.Select.Case> cases = new ArrayList<>();
            for (Map.Entry<String, Node> entry : children.entrySet()) {
                cases.add(new Selector.Select.CustomModelData.Entry(entry.getKey(), entry.getValue().toSelector()));
            }

            return new Selector.Select(new Selector.Select.CustomModelData(cases), leaf != null ? leaf : new Selector.Empty());
        }
    }

    /**
     * Configures a model element for vertical display.
     *
     * @param m The model to modify.
     */
    private static void guiItemModelVertical(Model m) {
        Model.Element e = m.addElement(new float[]{-1, 0, 8}, new float[]{17, 18, 9});
        e.face("north", new Model.Face("#0").uv(0, 0, 16, 16));
        e.face("south", new Model.Face("#0").uv(16, 0, 0, 16));
        e.face("east", new Model.Face("#0").uv(15, 0, 16, 16));
        e.face("west", new Model.Face("#0").uv(0, 0, 1, 16));
        e.face("up", new Model.Face("#0").uv(0, 0, 16, 1));
        e.face("down", new Model.Face("#0").uv(0, 15, 16, 1));
    }

    /**
     * Configures a model element for horizontal display.
     *
     * @param m The model to modify.
     */
    private static void guiItemModelHorizontal(Model m) {
        Model.Element e = m.addElement(new float[]{-1, 0, 8}, new float[]{17, 18, 9});
        e.face("north", new Model.Face("#0").uv(0, 0, 16, 16).rotation(270));
        e.face("south", new Model.Face("#0").uv(0, 16, 16, 0).rotation(270));
        e.face("east", new Model.Face("#0").uv(0, 0, 16, 1).rotation(270));
        e.face("west", new Model.Face("#0").uv(0, 15, 16, 16).rotation(270));
        e.face("up", new Model.Face("#0").uv(0, 0, 1, 16).rotation(90));
        e.face("down", new Model.Face("#0").uv(16, 0, 15, 16).rotation(90));
    }
}