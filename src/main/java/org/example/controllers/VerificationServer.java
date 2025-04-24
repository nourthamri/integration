package org.example.controllers;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.example.services.ServiceUser;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class VerificationServer {
    public static void startServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8085), 0);
        server.createContext("/verify", new VerifyHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("✅ Email Verification Server running on http://localhost:8085/");
    }

    static class VerifyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = parseQuery(query);
                String email = params.get("email");

                if (email == null || email.isEmpty()) {
                    String response = "❌ Email manquant.";
                    exchange.sendResponseHeaders(400, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                    return;
                }

                email = java.net.URLDecoder.decode(email, "UTF-8");

                ServiceUser userService = new ServiceUser();
                boolean success = userService.verifyUser(email);

                String response = success ? "✅ Votre compte est maintenant vérifié !" : "❌ Échec de la vérification.";
                exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }


        private Map<String, String> parseQuery(String query) {
            if (query == null || query.isEmpty()) {
                return Map.of();
            }
            return Arrays.stream(query.split("&"))
                    .map(param -> param.split("="))
                    .filter(pair -> pair.length == 2)
                    .collect(Collectors.toMap(pair -> pair[0], pair -> pair[1]));
        }
    }
}