package com.example.grpcdemo.helloworld.server;

import com.example.grpcdemo.helloworld.GreeterGrpc;
import com.example.grpcdemo.helloworld.HelloReply;
import com.example.grpcdemo.helloworld.HelloRequest;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class HelloWorldServer {

    private int port = 50051;

    private Server server;

    private void start() throws IOException {
        server = ServerBuilder.forPort(port).addService(new GreeterImpl()).build().start();
        log.info("Server started! listen on {}", port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("-----> shutting down gRPC server since JVM is shutting down <-----");
                HelloWorldServer.this.stop();
                System.out.println("-----> server shut down <-----");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final HelloWorldServer server = new HelloWorldServer();
        server.start();
        server.blockUntilShutdown();
    }

    static class GreeterImpl extends GreeterGrpc.GreeterImplBase {
        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            HelloReply reply = HelloReply.newBuilder().setMessage("---> Hello " + request.getName() + " <---").build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
