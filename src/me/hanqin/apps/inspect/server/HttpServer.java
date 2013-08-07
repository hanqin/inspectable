package me.hanqin.apps.inspect.server;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class HttpServer extends NanoHTTPD {

    private static HttpServer instance;
    private DefaultRequestDispatcher requestDispatcher;

    public HttpServer(int port) {
        super(port);
    }

    public HttpServer(String hostname, int port) {
        super(hostname, port);
    }

    public synchronized static HttpServer instantiate(int testServerPort) {
        if (instance != null) {
            throw new IllegalStateException("Can only instantiate once!");
        }
        instance = new HttpServer(testServerPort);
        return instance;
    }

    public synchronized static HttpServer getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Must be initialized!");
        }
        return instance;
    }

    @Override
    public Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> parms, Map<String, String> files) {
        return requestDispatcher.dispatch(uri, method, headers, parms, files);
    }

    public void setRequestDispatcher(DefaultRequestDispatcher requestDispatcher) {
        this.requestDispatcher = requestDispatcher;
    }
}
