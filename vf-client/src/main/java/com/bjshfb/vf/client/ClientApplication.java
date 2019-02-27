package com.bjshfb.vf.client;

import com.bjshfb.vf.client.expos.env.EnvConfig;
import com.bjshfb.vf.client.expos.nettycli.client.ClientSocket;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.bjshfb.vf.client")
public class ClientApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    /**
     * 服务启动之后 启动netty的客户端连接
     * */
    @Override
    public void run(String... args) throws Exception {
        new ClientSocket().run(5555, EnvConfig.NettyAddr);
//        new ClientSocket().run(5555, "192.168.1.200");
    }
}