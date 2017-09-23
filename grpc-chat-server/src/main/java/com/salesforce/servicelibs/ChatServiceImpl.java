package com.salesforce.servicelibs;

import com.google.protobuf.Empty;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;

import java.util.Observable;
import java.util.Observer;

public class ChatServiceImpl extends ChatGrpc.ChatImplBase {
    private Observable messageObservable = new Observable() {
        @Override
        public void notifyObservers(Object arg) {
            super.setChanged();
            super.notifyObservers(arg);
        }
    };

    @Override
    public void postMessage(ChatProto.ChatMessage request, StreamObserver<Empty> responseObserver) {
        System.out.printf("%s: %s\n", request.getAuthor(), request.getMessage());
        messageObservable.notifyObservers(request);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void getMessages(Empty request, StreamObserver<ChatProto.ChatMessage> responseObserver) {
        ServerCallStreamObserver<ChatProto.ChatMessage> serverResponseObserver = (ServerCallStreamObserver<ChatProto.ChatMessage>) responseObserver;

        Observer messageObserver = (o, arg) -> {
            serverResponseObserver.onNext((ChatProto.ChatMessage) arg);
        };

        serverResponseObserver.setOnCancelHandler(() -> {
            messageObservable.deleteObserver(messageObserver);
        });

        messageObservable.addObserver(messageObserver);
    }
}
