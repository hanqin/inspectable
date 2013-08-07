package me.hanqin.apps.inspect.util;

import android.database.Cursor;

import java.util.List;

public class JSONUtil {

    public static String listToJson(String key, List<String> values) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("'" + key + "': [");
        for (String database : values) {
            builder.append("'").append(database).append("',");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append("]");
        builder.append("}");
        return builder.toString();
    }

    public static String cursorToJson(Cursor cursor) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"result\": [");
        boolean hasResult = false;
        while (cursor.moveToNext()) {
            hasResult = true;
            int columnCount = cursor.getColumnCount();

            builder.append("{");
            for (int i = 0; i < columnCount; i++) {
                builder.append(String.format("\"%s\": \"%s\"", cursor.getColumnName(i), cursor.getString(i))).append(",");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append("},");
        }
        if (hasResult) {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("]}");
        cursor.close();
        return builder.toString();
    }
}
