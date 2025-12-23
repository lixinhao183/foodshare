package xinhao.foodshare;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("xinhao.foodshare.mapper")
@SpringBootApplication
public class FoodshareApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodshareApplication.class, args);
    }

}
