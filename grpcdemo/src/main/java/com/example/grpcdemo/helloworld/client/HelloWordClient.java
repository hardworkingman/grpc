package com.example.grpcdemo.helloworld.client;

import com.example.grpcdemo.helloworld.GreeterGrpc;
import com.example.grpcdemo.helloworld.HelloReply;
import com.example.grpcdemo.helloworld.HelloRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HelloWordClient {

    private final ManagedChannel managedChannel;
    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    public HelloWordClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext().build());
    }

    private HelloWordClient(ManagedChannel managedChannel) {
        this.managedChannel = managedChannel;
        this.blockingStub = GreeterGrpc.newBlockingStub(managedChannel);
    }

    public void greet(String name) {
        log.info("{} will try to greet server", name);
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply reply;
        try {
            reply = blockingStub.sayHello(request);
        } catch (StatusRuntimeException e) {
            log.warn("rpc failed: {}", e.getStatus());
            return;
        }
        log.info("Greeting {}", reply.getMessage());
    }

    public void shutdown() throws InterruptedException {
        managedChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws Exception {
        HelloWordClient client = new HelloWordClient("localhost", 50051);
        try {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String nextLine = scanner.nextLine();
                if (nextLine.isEmpty()) {
                    break;
                }
                client.greet(nextLine);
            }
        } finally {
            client.shutdown();
        }
    }

}
