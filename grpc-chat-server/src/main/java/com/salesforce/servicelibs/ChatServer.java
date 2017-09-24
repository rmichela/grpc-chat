package com.salesforce.servicelibs;

import com.salesforce.grpc.contrib.Servers;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class ChatServer {
    private static int PORT = 9999;

    public static void main(String[] args) throws Exception {
        // Create a server object
        Server server = ServerBuilder
                .forPort(PORT)
                .addService(new ChatServiceImpl())
                .build();

        // Start the server
        Servers.shutdownWithJvm(server, 1000);
        server.start();

        // Wait for SIGTERM
        System.out.println("Listening on port " + server.getPort());
        Thread.currentThread().join();
    }
}
