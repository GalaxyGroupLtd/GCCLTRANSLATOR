package com.carpa.library.utilities;

import java.util.LinkedList;
import java.util.List;

public class TableGenerator {
    List<String[]> rows = new LinkedList<String[]>();

    public void addRow(String... cols) {
        rows.add(cols);
    }

    private int[] colWidths() {
        int cols = -1;

        for (String[] row : rows)
            cols = Math.max(cols, row.length);

        int[] widths = new int[cols];

        for (String[] row : rows) {
            for (int colNum = 0; colNum < row.length; colNum++) {
                widths[colNum] = Math.max(widths[colNum], row[colNum].length());
            }
        }

        return widths;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        int[] colWidths = colWidths();

        for (String[] row : rows) {
            for (int colNum = 0; colNum < row.length; colNum++) {
                //added code due to me not want to use Apache common strings
                String spaces = "";
                String value = row[colNum] == null ? "" : row[colNum];
                for (int spac = colWidths[colNum] - value.length() - 1; spac >= 0; spac--) {
                    spaces += " ";
                }

                //buf.append(StringUtils.rightPad(StringUtils.defaultString(row[colNum]), colWidths[colNum]));
                buf.append(value).append(spaces);
                buf.append(' ');
            }

            buf.append('\n');
        }

        return buf.toString();
    }
}