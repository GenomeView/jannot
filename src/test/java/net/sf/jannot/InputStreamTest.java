package net.sf.jannot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;

import org.junit.Test;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Tests if we can see actual size and current read psoition of input streams
 */
public class InputStreamTest {
	private HttpServer server;

	@Test
	public void readLargeURL() throws IOException {
		startServer();
		URL bigfile = new URL("http://localhost:8000");
		InputStream stream = bigfile.openStream();

		System.out.println(stream.available());
		System.out.println("" + stream.read());

		System.out.println(stream.available());

		stopServer();
	}

	private void startServer() throws IOException {
		// Create an HttpServer instance on port 8000
		server = HttpServer.create(new InetSocketAddress(8000), 0);

		// Define a context that serves files from the current directory
		server.createContext("/", new HttpHandler() {
			@Override
			public void handle(HttpExchange e) throws IOException {
				byte[] response = "THIS IS OUR MESSAGE".getBytes();
				e.sendResponseHeaders(200, response.length);
				OutputStream os = e.getResponseBody();
				os.write(response);
				os.close(); // Handle requests here (simple file serving)
			}
		});

		// Start the server
		server.start();
		System.out.println("Server started on port 8000");
	}

	public void stopServer() {
		server.stop(0);
	}
}
