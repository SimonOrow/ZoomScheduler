package com.simonorow.zoomscheduler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class WebServer {

    private static String currentToken = "";
    private static Boolean receivedToken = false;
    private static WebServer single_instance = null;

    public static WebServer getInstance() {
        if (single_instance == null) {
            single_instance = new WebServer();
        }

        return single_instance;
    }

    public static void setToken(String token) {
        currentToken = token;
        receivedToken = true;
    }

    public static void startServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(4000), 0);
        server.createContext("/zoom", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    // https://zoom.us/oauth/authorize?response_type=code&client_id=_wOXBkLTE6Uti7WNjHtnw&redirect_uri=http://localhost:4000/zoom
    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            // Get code param
            Map<String, String> params = queryToMap(t.getRequestURI().getQuery());
            String response = "Received code " + params.get("code");
            // Respond with HTTP status code 200 and close the connection.
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();

            // Update token.
            currentToken = params.get("code");

        }
    }

    // Used to obtain GET parameters
    public static Map<String, String> queryToMap(String query) {
        if(query == null) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            }else{
                result.put(entry[0], "");
            }
        }
        return result;
    }
}
