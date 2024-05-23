package org.speiser.haggadah;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * WebServer.
 * 
 * @author Baruch Speiser. All rights reserved. 
 */
@SuppressWarnings("all")
public class WebServer {
  
  /** @param args */
  public static void main(String args[]) {
    try {
      HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
      server.createContext("/", new HttpHandler() {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
          try {
            if(!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
              respond(exchange, 405, "Only GET requests are supported".getBytes());
              return;
            }
            String path = exchange.getRequestURI().getPath();
            File f = new File("src/main/webapp" + path);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            if(path.equals("/")) {
              bos.write(Files.readAllBytes(Paths.get("src/main/webapp/include/header.html")));
              //Append all content
              File[] files = f.listFiles();
              Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File a, File b) {
                  return a.getName().compareTo(b.getName());
                }
              });
              for(File file : files) {
                if(file.isFile()) {
                  bos.write(Files.readAllBytes(Paths.get(file.toURI())));
                }
              }
              bos.write(Files.readAllBytes(Paths.get("src/main/webapp/include/footer.html")));
              byte[] response = bos.toByteArray();
              respond(exchange, 200, response);
            } else 
            if(f.exists()) {
              if(f.getName().endsWith(".html")) bos.write(Files.readAllBytes(Paths.get("src/main/webapp/include/header.html")));
              bos.write(Files.readAllBytes(Paths.get(f.toURI())));
              if(f.getName().endsWith(".html")) bos.write(Files.readAllBytes(Paths.get("src/main/webapp/include/footer.html")));
              byte[] response = bos.toByteArray();
              respond(exchange, 200, response);
            } else {
              respond(exchange, 404, "Resource not found: ".concat(path).getBytes());
            }
          } catch(Exception e) {
            e.printStackTrace();
            respond(exchange, 500, e.getMessage().getBytes());
          }
        }

        private void respond(HttpExchange exchange, int statusCode, byte[] response) throws IOException {
          exchange.sendResponseHeaders(statusCode, response.length);
          OutputStream os = exchange.getResponseBody();
          os.write(response, 0, response.length);
          os.close();
          exchange.close();
        }
      });
      server.setExecutor(Executors.newCachedThreadPool());
      server.start();
      System.out.println("Started.");
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
}
