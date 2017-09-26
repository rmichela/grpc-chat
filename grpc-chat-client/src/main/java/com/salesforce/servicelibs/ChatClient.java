package com.salesforce.servicelibs;

import com.google.protobuf.Empty;
import com.salesforce.grpc.contrib.LambdaStreamObserver;
import com.salesforce.grpc.contrib.MoreTimestamps;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jline.console.ConsoleReader;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static com.salesforce.servicelibs.ConsoleUtil.*;

public class ChatClient {
    private static final int PORT = 9999;

    public static void main(String[] args) throws Exception {
        // Set up channels and client stubs
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", PORT)
                .usePlaintext(true)
                .build();
        ChatGrpc.ChatBlockingStub blockingStub = ChatGrpc.newBlockingStub(channel);
        ChatGrpc.ChatStub streamStub = ChatGrpc.newStub(channel);

        ConsoleReader console = new ConsoleReader();

        // Prompt the user for their name
        console.println("Press ctrl+D to quit");
        String author = console.readLine("Who are you? > ");
        blockingStub.postMessage(toMessage(author, author + " joined."));

        // Subscribe to incoming messages
        streamStub.getMessages(Empty.getDefaultInstance(), new LambdaStreamObserver<>(
            // OnNext()
            chatMessage -> {
                // Don't print our own messages
                if (!chatMessage.getAuthor().equals(author)) {
                    printLine(console, chatMessage.getAuthor(), chatMessage.getMessage());
                }
            },
            // OnError()
            throwable -> {
                printLine(console, "ERROR", throwable.getMessage());
                System.exit(1);
            }
        ));

        // Publish outgoing messages
        String message;
        while ((message = console.readLine(author + " > ")) != null) {
            blockingStub.postMessage(toMessage(author, message));
        }

        // Log out and shutdown
        blockingStub.postMessage(toMessage(author, "left."));
        channel.shutdown();
        channel.awaitTermination(1, TimeUnit.SECONDS);
        console.getTerminal().restore();
    }

    private static ChatProto.ChatMessage toMessage(String author, String message) {
        return ChatProto.ChatMessage.newBuilder()
            .setWhen(MoreTimestamps.fromInstantUtc(Instant.now()))
            .setAuthor(author)
            .setMessage(message)
            .build();
    }
}
