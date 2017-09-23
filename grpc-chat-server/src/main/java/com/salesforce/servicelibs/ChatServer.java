package com.salesforce.servicelibs;

import com.salesforce.grpc.contrib.Servers;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class ChatServer {
    private static int PORT = 9999;

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder
                .forPort(PORT)
                .addService(new ChatServiceImpl())
                .build();

        Servers.shutdownWithJvm(server, 1000);
        server.start();

        System.out.println("Listening on port " + server.getPort());
        Thread.currentThread().join();
    }
}
