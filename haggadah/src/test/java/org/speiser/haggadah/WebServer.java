package org.speiser.haggadah;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.util.ServerRunner;

/**
 * WebServer.
 * 
 * @author Baruch Speiser. All rights reserved. 
 */
@SuppressWarnings("all")
public class WebServer extends NanoHTTPD {
  
  public WebServer() {
    super(8080);
  }
  
  @Override
  public Response serve(IHTTPSession session) {
    try {
      if(session.getMethod() != NanoHTTPD.Method.GET) {
        return NanoHTTPD.newFixedLengthResponse(
            Response.Status.METHOD_NOT_ALLOWED, 
            MIME_PLAINTEXT, 
            "Only GET requests are supported");
      }
      String path = session.getUri();
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
        return NanoHTTPD.newFixedLengthResponse(
            Response.Status.OK, 
            MIME_HTML, 
            new String(response, StandardCharsets.UTF_8));
      } else 
      if(f.exists()) {
        if(f.getName().endsWith(".html")) bos.write(Files.readAllBytes(Paths.get("src/main/webapp/include/header.html")));
        bos.write(Files.readAllBytes(Paths.get(f.toURI())));
        if(f.getName().endsWith(".html")) bos.write(Files.readAllBytes(Paths.get("src/main/webapp/include/footer.html")));
        byte[] response = bos.toByteArray();
        return NanoHTTPD.newFixedLengthResponse(
            Response.Status.OK, 
            MIME_HTML, 
            new String(response, StandardCharsets.UTF_8));
      } else {
        return NanoHTTPD.newFixedLengthResponse(
            Response.Status.NOT_FOUND, 
            MIME_PLAINTEXT, 
            "Resource not found: ".concat(path));
      }
    } catch(Exception e) {
      e.printStackTrace();
      return NanoHTTPD.newFixedLengthResponse(
          Response.Status.INTERNAL_ERROR, 
          MIME_PLAINTEXT, 
          e.getMessage());
    }
  }

  /** @param args */
  public static void main(String args[]) {
    try {
      ServerRunner.run(WebServer.class);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
}
