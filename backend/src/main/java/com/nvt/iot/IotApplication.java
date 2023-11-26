package com.nvt.iot;

import com.nvt.iot.repository.ConnectedDeviceRepository;
import com.nvt.iot.repository.ConnectedUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;

@SpringBootApplication
@RequiredArgsConstructor
public class IotApplication implements CommandLineRunner {
    @Lazy
    private final ConnectedDeviceRepository connectedDeviceRepository;
    @Lazy
    private final ConnectedUserRepository connectedUserRepository;

    public static void main(String[] args) {
        SpringApplication.run(IotApplication.class, args);
    }

    @Override
    public void run(String... args) {
        connectedDeviceRepository.deleteAll();
        connectedUserRepository.deleteAll();
    }
}
