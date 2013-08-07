package me.hanqin.apps.inspect.handlers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fi.iki.elonen.NanoHTTPD;

import static me.hanqin.apps.inspect.util.JSONUtil.cursorToJson;
import static me.hanqin.apps.inspect.util.JSONUtil.listToJson;
import static fi.iki.elonen.NanoHTTPD.Method.GET;
import static fi.iki.elonen.NanoHTTPD.Method.POST;
import static fi.iki.elonen.NanoHTTPD.Response;
import static fi.iki.elonen.NanoHTTPD.Response.Status.OK;
import static java.lang.String.format;

public class DatabaseHandler extends BaseHandler {

    private final Pattern DB_ROOT = Pattern.compile("/database/?");
    private final Pattern DB_INSPECT = Pattern.compile("/database/.+\\.db/?");

    public DatabaseHandler(Context targetContext) {
        super(targetContext);
    }

    @Override
    public Response handle(String uri, NanoHTTPD.Method method, Map<String, String> headers, Map<String, String> params, Map<String, String> files) {
        if (DB_ROOT.matcher(uri).matches())
            return new Response(OK, "application/json;charset=utf-8", listAllDbFiles());
        if (DB_INSPECT.matcher(uri).matches()) {
            if (GET.equals(method))
                return new Response(OK, "application/json;charset=utf-8", listTablesInDb(extractDbName(uri)));
            if (POST.equals(method))
                return new Response(OK, "application/json;charset=utf-8", queryOrUpdateDb(extractDbName(uri), params));
        }

        throw new UnsupportedOperationException();
    }

    private String queryOrUpdateDb(String dbName, Map<String, String> params) {
        SQLiteDatabase database = targetContext.openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);

        try {
            if (params.containsKey(PARAM_KEY_BULK_OPERATIONS) && params.get(PARAM_KEY_BULK_OPERATIONS).contains("true")) {
                return bulkOperations(database, params.get(PARAM_KEY_SQL));
            }

            return cursorToJson(database.rawQuery(params.get(PARAM_KEY_SQL), null));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        throw new IllegalStateException();
    }

    private static final Pattern DATE_TIME_PATTERN = Pattern.compile("\\(\\d*\\)");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d+");

    private String convertDateColumnInSql(String response) {
        Matcher matcher = DATE_TIME_PATTERN.matcher(response);
        String toReplace = response;

        while (matcher.find()) {
            String group = matcher.group();
            toReplace = toReplace.replace(group, dateString(group));
        }

        return toReplace;
    }

    private String dateString(String input) {
        Matcher matcher = DIGIT_PATTERN.matcher(input);
        Calendar instance = Calendar.getInstance();
        if (matcher.find()) {
            instance.add(Calendar.MINUTE, -Integer.parseInt(matcher.group()));
        }

        return String.valueOf(instance.getTime().getTime());
    }

    private String bulkOperations(SQLiteDatabase database, String sql) {
        String[] insertScripts = sql.split(";");
        for (String insertScript : insertScripts) {
            database.execSQL(convertDateColumnInSql(insertScript) + ";");
        }
        return format("{\"result\": \"%s lines changed\"}", insertScripts.length);
    }

    private String extractDbName(String uri) {
        return uri.split("/")[2];
    }

    private String listTablesInDb(String dbName) {
        SQLiteDatabase database = targetContext.openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("SELECT * FROM sqlite_master WHERE type='table';", null);

        List<String> tables = new ArrayList<String>();
        try {
            while (cursor.moveToNext()) {
                tables.add(cursor.getString(cursor.getColumnIndex("name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            database.close();
        }

        return listToJson("tables", tables);
    }

    private String listAllDbFiles() {
        String[] databases = targetContext.databaseList();
        String key = "databaseList";
        return listToJson(key, Arrays.asList(databases));
    }

}
