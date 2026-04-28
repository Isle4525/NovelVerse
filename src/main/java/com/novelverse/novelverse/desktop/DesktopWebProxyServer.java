package com.novelverse.novelverse.desktop;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class DesktopWebProxyServer {

    private static final String COMPAT_SCRIPT = """
            <script>
            if (!window.lucide) {
              window.lucide = { createIcons: function() {} };
            }
            if (!window.refreshIcons) {
              window.refreshIcons = function() {
                if (window.lucide && typeof window.lucide.createIcons === 'function') {
                  window.lucide.createIcons();
                }
              };
            }
            if (!String.prototype.replaceAll) {
              String.prototype.replaceAll = function(search, replacement) {
                return this.split(search).join(replacement);
              };
            }
            if (!window.fetch) {
              window.fetch = function(url, options) {
                options = options || {};
                return new Promise(function(resolve, reject) {
                  var xhr = new XMLHttpRequest();
                  xhr.open(options.method || 'GET', url, true);
                  if (options.headers) {
                    Object.keys(options.headers).forEach(function(key) {
                      xhr.setRequestHeader(key, options.headers[key]);
                    });
                  }
                  xhr.onload = function() {
                    resolve({
                      ok: xhr.status >= 200 && xhr.status < 300,
                      status: xhr.status,
                      text: function() { return Promise.resolve(xhr.responseText); },
                      json: function() { return Promise.resolve(xhr.responseText ? JSON.parse(xhr.responseText) : null); }
                    });
                  };
                  xhr.onerror = function() { reject(new Error('Network request failed')); };
                  xhr.send(options.body || null);
                });
              };
            }
            </script>
            """;

    private final String targetBaseUrl;
    private final int port;
    private final ExecutorService executorService;
    private HttpServer server;

    public DesktopWebProxyServer(String targetBaseUrl, int port, ExecutorService executorService) {
        this.targetBaseUrl = targetBaseUrl;
        this.port = port;
        this.executorService = executorService;
    }

    public void start() throws IOException {
        if (server != null) {
            return;
        }

        server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.createContext("/", new ProxyHandler());
        server.setExecutor(executorService);
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
    }

    public String getBaseUrl() {
        return "http://127.0.0.1:" + port;
    }

    private class ProxyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String rawPath = exchange.getRequestURI().getRawPath();
            String path = (rawPath == null || rawPath.isBlank()) ? "/" : rawPath;
            String query = exchange.getRequestURI().getRawQuery();
            String targetUrl = targetBaseUrl + path + (query != null ? "?" + query : "");

            HttpURLConnection connection = (HttpURLConnection) new URL(targetUrl).openConnection();
            connection.setRequestMethod(exchange.getRequestMethod());
            connection.setInstanceFollowRedirects(false);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(15000);

            copyRequestHeaders(exchange.getRequestHeaders(), connection);

            byte[] requestBody = exchange.getRequestBody().readAllBytes();
            if (requestBody.length > 0) {
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(requestBody);
                }
            }

            int status = connection.getResponseCode();
            InputStream responseStream = status >= 400 ? connection.getErrorStream() : connection.getInputStream();
            byte[] responseBytes = responseStream != null ? responseStream.readAllBytes() : new byte[0];

            Headers headers = exchange.getResponseHeaders();
            copyResponseHeaders(connection.getHeaderFields(), headers);

            String contentType = connection.getContentType();
            if (contentType != null && contentType.contains("text/html")) {
                String html = new String(responseBytes, StandardCharsets.UTF_8);
                responseBytes = injectCompatScript(html).getBytes(StandardCharsets.UTF_8);
                headers.set("Content-Type", "text/html; charset=UTF-8");
            }

            exchange.sendResponseHeaders(status, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }

        private void copyRequestHeaders(Headers from, HttpURLConnection to) {
            for (Map.Entry<String, List<String>> entry : from.entrySet()) {
                String key = entry.getKey();
                if (key == null || isSkippedHeader(key)) {
                    continue;
                }
                for (String value : entry.getValue()) {
                    to.addRequestProperty(key, value);
                }
            }
        }

        private void copyResponseHeaders(Map<String, List<String>> from, Headers to) {
            for (Map.Entry<String, List<String>> entry : from.entrySet()) {
                String key = entry.getKey();
                if (key == null || isSkippedHeader(key)) {
                    continue;
                }
                to.put(key, entry.getValue());
            }
        }

        private boolean isSkippedHeader(String key) {
            String normalized = key.toLowerCase();
            return normalized.equals("host")
                    || normalized.equals("connection")
                    || normalized.equals("content-length")
                    || normalized.equals("transfer-encoding");
        }

        private String injectCompatScript(String html) {
            int headIndex = html.indexOf("<head>");
            if (headIndex >= 0) {
                return html.substring(0, headIndex + 6) + COMPAT_SCRIPT + html.substring(headIndex + 6);
            }
            return COMPAT_SCRIPT + html;
        }
    }
}
