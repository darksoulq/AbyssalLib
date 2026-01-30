package com.github.darksoulq.abyssallib.world.gui;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for calculating inventory slot positions.
 * <p>
 * This class provides helper methods to generate collections of {@link SlotPosition}
 * based on geometric patterns such as rows, columns, grids, and borders.
 */
public final class SlotUtil {

    /**
     * Private constructor to prevent instantiation.
     */
    private SlotUtil() {}

    /**
     * Generates a list of positions forming the border of a rectangular area.
     *
     * @param segment   the inventory segment (TOP or BOTTOM)
     * @param startSlot the top-left slot index to start from
     * @param rows      the height of the rectangle in rows
     * @param cols      the width of the rectangle in columns
     * @param maxRows   the maximum number of rows in the inventory
     * @param maxCols   the maximum number of columns in the inventory
     * @return a list of slot positions forming the outline
     */
    public static List<SlotPosition> border(GuiView.Segment segment, int startSlot, int rows, int cols, int maxRows, int maxCols) {
        List<SlotPosition> positions = new ArrayList<>();
        int startRow = startSlot / maxCols;
        int startCol = startSlot % maxCols;

        for (int r = 0; r < rows; r++) {
            int row = startRow + r;
            if (row >= maxRows) break;
            for (int c = 0; c < cols; c++) {
                int col = startCol + c;
                if (col >= maxCols) continue;

                boolean isEdge = r == 0 || r == rows - 1 || c == 0 || c == cols - 1;
                if (isEdge) positions.add(new SlotPosition(segment, row * maxCols + col));
            }
        }
        return positions;
    }

    /**
     * Generates a list of positions forming a horizontal row.
     *
     * @param segment   the inventory segment (TOP or BOTTOM)
     * @param startSlot the slot index where the row begins
     * @param length    the number of slots in the row
     * @param maxCols   the maximum number of columns in the inventory
     * @return a list of slot positions in the row
     */
    public static List<SlotPosition> row(GuiView.Segment segment, int startSlot, int length, int maxCols) {
        List<SlotPosition> positions = new ArrayList<>();
        int startRow = startSlot / maxCols;
        int startCol = startSlot % maxCols;

        for (int i = 0; i < length; i++) {
            int col = startCol + i;
            if (col >= maxCols) break;
            int slot = startRow * maxCols + col;
            positions.add(new SlotPosition(segment, slot));
        }
        return positions;
    }

    /**
     * Generates a list of positions forming a vertical column.
     *
     * @param segment   the inventory segment (TOP or BOTTOM)
     * @param startSlot the slot index where the column begins
     * @param step      the vertical increment between slots (usually 1)
     * @param length    the number of slots in the column
     * @param maxRows   the maximum number of rows in the inventory
     * @param maxCols   the maximum number of columns in the inventory
     * @return a list of slot positions in the column
     */
    public static List<SlotPosition> column(GuiView.Segment segment, int startSlot, int step, int length, int maxRows, int maxCols) {
        List<SlotPosition> positions = new ArrayList<>();
        int startRow = startSlot / maxCols;
        int startCol = startSlot % maxCols;

        for (int i = 0; i < length; i++) {
            int row = startRow + i * step;
            if (row >= maxRows) break;
            int slot = row * maxCols + startCol;
            positions.add(new SlotPosition(segment, slot));
        }
        return positions;
    }

    /**
     * Generates a list of positions forming a solid rectangular grid.
     *
     * @param segment   the inventory segment (TOP or BOTTOM)
     * @param startSlot the top-left slot index of the grid
     * @param rows      the number of rows in the grid
     * @param cols      the number of columns in the grid
     * @param maxRows   the maximum number of rows in the inventory
     * @param maxCols   the maximum number of columns in the inventory
     * @return a list of all slot positions within the grid
     */
    public static List<SlotPosition> grid(GuiView.Segment segment, int startSlot, int rows, int cols, int maxRows, int maxCols) {
        List<SlotPosition> positions = new ArrayList<>();
        int startRow = startSlot / maxCols;
        int startCol = startSlot % maxCols;

        for (int r = 0; r < rows; r++) {
            int row = startRow + r;
            if (row >= maxRows) break;
            for (int c = 0; c < cols; c++) {
                int col = startCol + c;
                if (col >= maxCols) continue;
                positions.add(new SlotPosition(segment, row * maxCols + col));
            }
        }
        return positions;
    }

    /**
     * Generates a list of positions based on a relative integer offset pattern.
     *
     * @param segment   the inventory segment (TOP or BOTTOM)
     * @param startSlot the reference slot index for the offsets
     * @param maxRows   the maximum number of rows in the inventory
     * @param maxCols   the maximum number of columns in the inventory
     * @param offsets   the integer offsets relative to the startSlot index
     * @return a list of validated slot positions
     */
    public static List<SlotPosition> pattern(GuiView.Segment segment, int startSlot, int maxRows, int maxCols, int... offsets) {
        List<SlotPosition> positions = new ArrayList<>();
        int startRow = startSlot / maxCols;
        int startCol = startSlot % maxCols;

        for (int offset : offsets) {
            int row = startRow + offset / maxCols;
            int col = startCol + offset % maxCols;
            if (row < 0 || row >= maxRows || col < 0 || col >= maxCols) continue;
            positions.add(new SlotPosition(segment, row * maxCols + col));
        }
        return positions;
    }
}