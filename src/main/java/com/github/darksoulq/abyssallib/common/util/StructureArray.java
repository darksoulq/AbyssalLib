package com.github.darksoulq.abyssallib.common.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * A utility class for managing and manipulating 2D structural grids of generic elements.
 * <p>
 * This class provides extensive factory methods for creating shapes (rectangles, borders, lines)
 * and supports transformations like rotation and flipping. It is primarily used to define
 * the physical layout of items within a GUI.
 *
 * @param <T> The type of elements contained within the structure.
 */
public final class StructureArray<T> {

    /**
     * Defines the primary axis for iteration and rendering logic.
     */
    public enum Orientation {
        /** Iterate through columns first, then move to the next row. */
        HORIZONTAL,
        /** Iterate through rows first, then move to the next column. */
        VERTICAL
    }

    /** The number of horizontal cells in the structure. */
    private final int width;

    /** The number of vertical cells in the structure. */
    private final int height;

    /** The flat array containing all elements in row-major order. */
    private final T[] elements;

    /** Precomputed indices for horizontal (row-major) iteration. */
    private final int[] rowMajorOrder;

    /** Precomputed indices for vertical (column-major) iteration. */
    private final int[] columnMajorOrder;

    /** The class type of the generic elements, used for array reflection. */
    private final Class<T> type;

    /**
     * Private constructor for internal use.
     *
     * @param type     The class of T.
     * @param width    Grid width.
     * @param height   Grid height.
     * @param elements Initialized flat array.
     */
    private StructureArray(Class<T> type, int width, int height, T[] elements) {
        this.type = type;
        this.width = width;
        this.height = height;
        this.elements = elements;
        this.rowMajorOrder = buildRowMajorOrder();
        this.columnMajorOrder = buildColumnMajorOrder();
    }

    /**
     * Reflectively creates a new generic array of type T.
     *
     * @param <T>  The generic type.
     * @param type The class type.
     * @param size Array length.
     * @return A new T[size] array.
     */
    @SuppressWarnings("unchecked")
    private static <T> T[] newArray(Class<T> type, int size) {
        return (T[]) Array.newInstance(type, size);
    }

    /**
     * Creates a structure from an existing flat array.
     *
     * @param <T>      Generic type.
     * @param type     The class type.
     * @param width    Grid width.
     * @param height   Grid height.
     * @param elements Flat array (must be width * height in size).
     * @return A new StructureArray.
     */
    public static <T> StructureArray<T> ofGrid(Class<T> type, int width, int height, T[] elements) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(elements);
        if (width <= 0 || height <= 0) throw new IllegalArgumentException("Dimensions must be positive");
        if (elements.length != width * height) throw new IllegalArgumentException("Array size mismatch");
        return new StructureArray<>(type, width, height, elements.clone());
    }

    /**
     * Creates a structure filled with elements provided by a supplier.
     *
     * @param <T>      Generic type.
     * @param type     The class type.
     * @param width    Grid width.
     * @param height   Grid height.
     * @param supplier A function providing the element for each cell.
     * @return A filled StructureArray.
     */
    public static <T> StructureArray<T> filled(Class<T> type, int width, int height, Supplier<T> supplier) {
        if (width <= 0 || height <= 0) throw new IllegalArgumentException();
        T[] arr = newArray(type, width * height);
        for (int i = 0; i < arr.length; i++) arr[i] = supplier.get();
        return new StructureArray<>(type, width, height, arr);
    }

    /**
     * Creates a structure filled with a static value.
     *
     * @param <T>    Generic type.
     * @param type   The class type.
     * @param width  Grid width.
     * @param height Grid height.
     * @param value  The object to place in every cell.
     * @return A filled StructureArray.
     */
    public static <T> StructureArray<T> filled(Class<T> type, int width, int height, T value) {
        return filled(type, width, height, (Supplier<T>) () -> value);
    }

    /**
     * Creates a structure using a generator based on coordinates.
     *
     * @param <T>       Generic type.
     * @param type      The class type.
     * @param width     Grid width.
     * @param height    Grid height.
     * @param generator Function receiving (x, y) and returning an element.
     * @return A generated StructureArray.
     */
    public static <T> StructureArray<T> generateGrid(Class<T> type, int width, int height, BiFunction<Integer,Integer,T> generator) {
        if (width <= 0 || height <= 0) throw new IllegalArgumentException();
        T[] arr = newArray(type, width * height);
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                arr[y * width + x] = generator.apply(x, y);
        return new StructureArray<>(type, width, height, arr);
    }

    /**
     * Creates a single-row structure.
     *
     * @param <T>      Generic type.
     * @param type     The class type.
     * @param elements Row elements.
     * @return A 1-high StructureArray.
     */
    @SafeVarargs
    public static <T> StructureArray<T> rowOf(Class<T> type, T... elements) {
        return ofGrid(type, elements.length, 1, elements);
    }

    /**
     * Creates a single-column structure.
     *
     * @param <T>      Generic type.
     * @param type     The class type.
     * @param elements Column elements.
     * @return A 1-wide StructureArray.
     */
    @SafeVarargs
    public static <T> StructureArray<T> columnOf(Class<T> type, T... elements) {
        return ofGrid(type, 1, elements.length, elements);
    }

    /**
     * Creates a 3-part row (Left, Middle..., Right).
     *
     * @param <T>    Generic type.
     * @param type   The class type.
     * @param length Row width.
     * @param left   Item at the far left.
     * @param middle Item(s) in the center.
     * @param right  Item at the far right.
     * @return A row StructureArray.
     */
    public static <T> StructureArray<T> row(Class<T> type, int length, T left, T middle, T right) {
        if (length <= 0) throw new IllegalArgumentException();
        T[] arr = newArray(type, length);
        if (length == 1) arr[0] = middle;
        else if (length == 2) {
            arr[0] = left; // Fixed logic: index 0 is left
            arr[1] = right;
        } else {
            arr[0] = left;
            arr[length - 1] = right;
            Arrays.fill(arr, 1, length - 1, middle);
        }
        return new StructureArray<>(type, length, 1, arr);
    }

    /**
     * Creates a 3-part column (Top, Middle..., Bottom).
     *
     * @param <T>    Generic type.
     * @param type   The class type.
     * @param height Column height.
     * @param top    Item at the very top.
     * @param middle Item(s) in the center.
     * @param bottom Item at the very bottom.
     * @return A column StructureArray.
     */
    public static <T> StructureArray<T> column(Class<T> type, int height, T top, T middle, T bottom) {
        if (height <= 0) throw new IllegalArgumentException();
        T[] arr = newArray(type, height);
        if (height == 1) arr[0] = middle;
        else if (height == 2) {
            arr[0] = top;
            arr[1] = bottom;
        } else {
            arr[0] = top;
            arr[height - 1] = bottom;
            Arrays.fill(arr, 1, height - 1, middle);
        }
        return new StructureArray<>(type, 1, height, arr);
    }

    /**
     * Creates a solid rectangle.
     *
     * @param <T>    Generic type.
     * @param type   The class type.
     * @param width  Width.
     * @param height Height.
     * @param fill   The filling element.
     * @return A rectangular StructureArray.
     */
    public static <T> StructureArray<T> rectangle(Class<T> type, int width, int height, T fill) {
        return filled(type, width, height, fill);
    }

    /**
     * Creates a solid square.
     *
     * @param <T>  Generic type.
     * @param type The class type.
     * @param size Side length.
     * @param fill The filling element.
     * @return A square StructureArray.
     */
    public static <T> StructureArray<T> square(Class<T> type, int size, T fill) {
        return rectangle(type, size, size, fill);
    }

    /**
     * Creates a complex rectangle with unique elements for corners, edges, and the center.
     *
     * @param <T>         Generic type.
     * @param type        The class type.
     * @param width       Width.
     * @param height      Height.
     * @param topLeft     Corner element.
     * @param topRight    Corner element.
     * @param bottomLeft  Corner element.
     * @param bottomRight Corner element.
     * @param topEdge     Edge element.
     * @param bottomEdge  Edge element.
     * @param leftEdge    Edge element.
     * @param rightEdge   Edge element.
     * @param center      The internal filling.
     * @return A bordered StructureArray.
     */
    public static <T> StructureArray<T> borderedRectangle(
        Class<T> type,
        int width,
        int height,
        T topLeft,
        T topRight,
        T bottomLeft,
        T bottomRight,
        T topEdge,
        T bottomEdge,
        T leftEdge,
        T rightEdge,
        T center
    ) {
        if (width <= 0 || height <= 0) throw new IllegalArgumentException();
        T[] arr = newArray(type, width * height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean top = y == 0;
                boolean bottom = y == height - 1;
                boolean left = x == 0;
                boolean right = x == width - 1;

                T v;
                if (top && left) v = topLeft;
                else if (top && right) v = topRight;
                else if (bottom && left) v = bottomLeft;
                else if (bottom && right) v = bottomRight;
                else if (top) v = topEdge;
                else if (bottom) v = bottomEdge;
                else if (left) v = leftEdge;
                else if (right) v = rightEdge;
                else v = center;

                arr[y * width + x] = v;
            }
        }
        return new StructureArray<>(type, width, height, arr);
    }

    /**
     * Creates a rectangle where corners and edges use the same element.
     *
     * @param <T>    Generic type.
     * @param type   The class type.
     * @param width  Width.
     * @param height Height.
     * @param border The border element.
     * @param inside The internal filling.
     * @return A hollow/bordered StructureArray.
     */
    public static <T> StructureArray<T> hollowRectangle(Class<T> type, int width, int height, T border, T inside) {
        return borderedRectangle(
            type,
            width, height,
            border, border,
            border, border,
            border, border,
            border, border,
            inside
        );
    }

    /**
     * Creates a structure with two columns.
     *
     * @param <T>    Generic type.
     * @param type   The class type.
     * @param height Height.
     * @param left   Left column value.
     * @param right  Right column value.
     * @return A 2-wide StructureArray.
     */
    public static <T> StructureArray<T> twoColumns(Class<T> type, int height, T left, T right) {
        return generateGrid(type, 2, height, (x,y) -> x == 0 ? left : right);
    }

    /**
     * Creates a structure with two rows.
     *
     * @param <T>    Generic type.
     * @param type   The class type.
     * @param width  Width.
     * @param top    Top row value.
     * @param bottom Bottom row value.
     * @return A 2-high StructureArray.
     */
    public static <T> StructureArray<T> twoRows(Class<T> type, int width, T top, T bottom) {
        return generateGrid(type, width, 2, (x,y) -> y == 0 ? top : bottom);
    }

    /**
     * Internal method to build indices for horizontal iteration (0, 1, 2, 3...).
     *
     * @return Integer array of sequential indices.
     */
    private int[] buildRowMajorOrder() {
        int[] order = new int[elements.length];
        for (int i = 0; i < order.length; i++) order[i] = i;
        return order;
    }

    /**
     * Internal method to build indices for vertical iteration.
     *
     * @return Integer array of indices sorted by columns.
     */
    private int[] buildColumnMajorOrder() {
        int[] order = new int[elements.length];
        int idx = 0;
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                order[idx++] = y * width + x;
        return order;
    }

    /**
     * Rotates the structure 90 degrees clockwise.
     *
     * @return A new rotated StructureArray.
     */
    public StructureArray<T> rotatedClockwise() {
        T[] arr = newArray(type, elements.length);
        int newW = height;
        int newH = width;
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                arr[x * newW + (newW - y - 1)] = elements[y * width + x];
        return new StructureArray<>(type, newW, newH, arr);
    }

    /**
     * Flips the grid horizontally.
     *
     * @return A new mirrored StructureArray.
     */
    public StructureArray<T> flippedHorizontally() {
        T[] arr = newArray(type, elements.length);
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                arr[y * width + (width - 1 - x)] = elements[y * width + x];
        return new StructureArray<>(type, width, height, arr);
    }

    /**
     * Flips the grid vertically.
     *
     * @return A new mirrored StructureArray.
     */
    public StructureArray<T> flippedVertically() {
        T[] arr = newArray(type, elements.length);
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                arr[(height - 1 - y) * width + x] = elements[y * width + x];
        return new StructureArray<>(type, width, height, arr);
    }

    /**
     * Retrieves the sequence of flat indices for a given iteration mode.
     *
     * @param orientation The desired orientation.
     * @return An array of integer indices.
     */
    public int[] iterationOrder(Orientation orientation) {
        return orientation == Orientation.HORIZONTAL ? rowMajorOrder : columnMajorOrder;
    }

    /**
     * Gets the element at a specific 2D coordinate.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return The element at (x, y).
     */
    public T elementAt(int x, int y) {
        return elements[y * width + x];
    }

    /**
     * Returns a copy of the internal flat array.
     *
     * @return T[] array.
     */
    public T[] toFlatArray() {
        return elements.clone();
    }

    /** @return Total horizontal cells. */
    public int width() { return width; }
    /** @return Total vertical cells. */
    public int height() { return height; }
    /** @return Total number of cells. */
    public int size() { return elements.length; }

    /**
     * Starts a coordinated builder for manual grid assembly.
     *
     * @param <T>    Type.
     * @param type   Class type.
     * @param width  Width.
     * @param height Height.
     * @return A new Builder.
     */
    public static <T> Builder<T> builder(Class<T> type, int width, int height) {
        if (width <= 0 || height <= 0) throw new IllegalArgumentException();
        T[] arr = newArray(type, width * height);
        return new Builder<>(type, width, height, arr);
    }

    /**
     * Starts a pattern-based builder for crafting grids using strings.
     *
     * @param <T>  Type.
     * @param type Class type.
     * @return A new PatternBuilder.
     */
    public static <T> PatternBuilder<T> patternBuilder(Class<T> type) {
        return new PatternBuilder<>(type);
    }

    /**
     * Fluent builder for manual cell assignment.
     *
     * @param <T> The element type.
     */
    public static final class Builder<T> {
        /** The grid width. */
        private final int width;
        /** The grid height. */
        private final int height;
        /** The working flat array. */
        private final T[] elements;
        /** The element class. */
        private final Class<T> type;

        private Builder(Class<T> type, int width, int height, T[] elements) {
            this.type = type;
            this.width = width;
            this.height = height;
            this.elements = elements;
        }

        /**
         * Sets an element at a specific coordinate.
         *
         * @param x     X.
         * @param y     Y.
         * @param value Element.
         * @return This builder.
         */
        public Builder<T> set(int x, int y, T value) {
            elements[y * width + x] = value;
            return this;
        }

        /**
         * Fills all current null/empty cells with a value.
         *
         * @param value Element.
         * @return This builder.
         */
        public Builder<T> fillAll(T value) {
            Arrays.fill(elements, value);
            return this;
        }

        /** @return A new StructureArray with current assignments. */
        public StructureArray<T> build() {
            return new StructureArray<>(type, width, height, elements.clone());
        }
    }

    /**
     * Fluent builder for mapping characters in a string pattern to objects.
     *
     * @param <T> The element type.
     */
    public static final class PatternBuilder<T> {
        /** The element class. */
        private final Class<T> type;
        /** The visual pattern rows. */
        private final List<String> pattern = new ArrayList<>();
        /** Mapping of characters to objects. */
        private final Map<Character, T> keys = new HashMap<>();

        private PatternBuilder(Class<T> type) {
            this.type = type;
        }

        /**
         * Defines the grid shape using strings. Each string is one row.
         *
         * @param rows The pattern strings.
         * @return This builder.
         */
        public PatternBuilder<T> pattern(String... rows) {
            this.pattern.addAll(Arrays.asList(rows));
            return this;
        }

        /**
         * Binds a character to an object.
         *
         * @param key   The character in the pattern.
         * @param value The object to place.
         * @return This builder.
         */
        public PatternBuilder<T> key(char key, T value) {
            this.keys.put(key, value);
            return this;
        }

        /**
         * Constructs the StructureArray.
         *
         * @return The resulting structure.
         * @throws IllegalStateException If pattern is empty or rows have inconsistent lengths.
         */
        public StructureArray<T> build() {
            if (pattern.isEmpty()) throw new IllegalStateException("Pattern is empty");
            int height = pattern.size();
            int width = pattern.get(0).length();

            for (String row : pattern) {
                if (row.length() != width) throw new IllegalStateException("Inconsistent row widths");
            }

            T[] arr = newArray(type, width * height);
            for (int y = 0; y < height; y++) {
                String row = pattern.get(y);
                for (int x = 0; x < width; x++) {
                    char c = row.charAt(x);
                    if (c != ' ') {
                        if (!keys.containsKey(c)) throw new IllegalStateException("Missing key: " + c);
                        arr[y * width + x] = keys.get(c);
                    }
                }
            }
            return new StructureArray<>(type, width, height, arr);
        }
    }
}