package com.nvt.iot;

import com.nvt.iot.repository.ConnectedDeviceRepository;
import com.nvt.iot.repository.ConnectedUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IotApplication implements CommandLineRunner {
	@Autowired
	private ConnectedDeviceRepository connectedDeviceRepository;
	@Autowired
	private ConnectedUserRepository connectedUserRepository;
	public static void main(String[] args) {
		SpringApplication.run(IotApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		connectedDeviceRepository.deleteAll();
		connectedUserRepository.deleteAll();
	}
}
