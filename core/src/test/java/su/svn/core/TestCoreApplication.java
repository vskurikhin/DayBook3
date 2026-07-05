package su.svn.core;

import org.springframework.boot.SpringApplication;

public class TestCoreApplication {

	public static void main(String[] args) {
		SpringApplication.from(CoreApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
