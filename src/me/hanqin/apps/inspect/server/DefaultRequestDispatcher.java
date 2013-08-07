package me.hanqin.apps.inspect.server;

import me.hanqin.apps.inspect.handlers.BaseHandler;

import java.util.HashMap;
import java.util.Map;

import static fi.iki.elonen.NanoHTTPD.MIME_PLAINTEXT;
import static fi.iki.elonen.NanoHTTPD.Method;
import static fi.iki.elonen.NanoHTTPD.Response;

public class DefaultRequestDispatcher {
    private HashMap<String, BaseHandler> handlers = new HashMap<String, BaseHandler>();

    public void setHandlers(HashMap<String, BaseHandler> handlers) {
        this.handlers = handlers;
    }

    public Response dispatch(String uri, Method method, Map<String, String> headers, Map<String, String> parms, Map<String, String> files) {
        logRequest(uri, method, headers, parms, files);

        try {
            if (uri.contains(BaseHandler.DISPATCH_KEY_DATABASE)) {
                return handlers.get(BaseHandler.DISPATCH_KEY_DATABASE).handle(uri, method, headers, parms, files);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Response(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Request not supported");
    }

    private void logRequest(String uri, Method method, Map<String, String> headers, Map<String, String> parms, Map<String, String> files) {
        System.out.println("uri = " + uri);
        System.out.println("method = " + method);
        for (String headerKey : headers.keySet()) {
            System.out.println("header = " + headerKey + " value = " + headers.get(headerKey));
        }
        for (String paramKey : parms.keySet()) {
            System.out.println("param = " + paramKey + " value = " + parms.get(paramKey));
        }
        for (String fileKey : files.keySet()) {
            System.out.println("file = " + fileKey + " value = " + files.get(fileKey));
        }
    }
}
