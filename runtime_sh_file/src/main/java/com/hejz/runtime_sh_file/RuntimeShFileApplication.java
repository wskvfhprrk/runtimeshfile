package com.hejz.runtime_sh_file;

import com.hejz.runtime_sh_file.netty.SystemClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RuntimeShFileApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(RuntimeShFileApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        SystemClient.run();
    }
}
