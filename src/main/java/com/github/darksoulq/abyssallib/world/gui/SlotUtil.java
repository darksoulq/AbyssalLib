package com.github.darksoulq.abyssallib.world.gui;

import java.util.ArrayList;
import java.util.List;

public final class SlotUtil {
    private SlotUtil() {}

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

    public static List<SlotPosition> column(GuiView.Segment segment, int startSlot, int step, int length, int maxRows, int maxCols) {
        List<SlotPosition> positions = new ArrayList<>();
        int startRow = startSlot / maxCols;
        int startCol = startSlot % maxCols;

        for (int i = 0; i < length; i++) {
            int row = startRow + i * step;
            if (row >= maxRows) break; // skip out of bounds
            int slot = row * maxCols + startCol;
            positions.add(new SlotPosition(segment, slot));
        }
        return positions;
    }

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
