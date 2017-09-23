Overview
========
This repo demonstrates a trivial gRPC chat service demonstrating unary and streaming rpc calls.

The server runs on port 9999;

Building
========
`mvn package`

Running
=======
**Server**
```
> cd grpc-chat-server/target
> java -jar grpc-chat-server-1.0-SNAPSHOT.jar
```

**Client**
```
> cd grpc-chat-client/target
> java -jar grpc-chat-client-1.0-SNAPSHOT.jar
```