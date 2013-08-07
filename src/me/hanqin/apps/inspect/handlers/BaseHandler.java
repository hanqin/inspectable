package me.hanqin.apps.inspect.handlers;

import android.content.Context;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

import static fi.iki.elonen.NanoHTTPD.Response;

public abstract class BaseHandler {
    public static final String DISPATCH_KEY_DATABASE = "database";
    public static final String PARAM_KEY_BULK_OPERATIONS = "bulk";
    public static final String PARAM_KEY_SQL = "sql";

    protected Context targetContext;

    public BaseHandler(Context targetContext) {
        this.targetContext = targetContext;
    }

    public abstract Response handle(String uri, NanoHTTPD.Method method, Map<String, String> headers, Map<String, String> params, Map<String, String> files);
}
