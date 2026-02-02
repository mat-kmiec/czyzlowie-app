package pl.czyzlowie;

import org.springframework.boot.SpringApplication;

public class TestCzyzlowieApplication {

    public static void main(String[] args) {
        SpringApplication.from(CzyzlowieApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
