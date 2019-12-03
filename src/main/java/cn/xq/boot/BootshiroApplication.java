package cn.xq.boot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.xq.boot.mapper")
public class BootshiroApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootshiroApplication.class, args);
    }

}
