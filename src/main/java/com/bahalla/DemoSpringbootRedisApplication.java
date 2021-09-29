package com.bahalla;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.Serializable;
import java.time.Instant;

@SpringBootApplication
public class DemoSpringbootRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoSpringbootRedisApplication.class, args);
    }

    @Bean
    public ApplicationRunner start(ExpensiveService expensiveService) {
        return args -> {

            var stopWatch = new StopWatch();
            var input = 54;
            time(expensiveService, stopWatch, input);
            time(expensiveService, stopWatch, input);
        };
    }

    private static Response time(ExpensiveService expensiveService, StopWatch stopWatch,
                                 double input) {

        stopWatch.start();
        Response response = expensiveService.performExpensiveCalculation(input);

        stopWatch.stop();
        System.out.println("Get response " + response.toString() + " After " + stopWatch.getLastTaskTimeMillis());

        return response;
    }
}

@Service
class ExpensiveService {

    @SneakyThrows
    @Cacheable("expensive")
    public Response performExpensiveCalculation(double input) {

        Thread.sleep(10 * 1000);
        return new Response("Response for input : "+ input + " @ " + Instant.now());
    }

}


@Data
@RequiredArgsConstructor
@ToString
class Response implements Serializable {
    final private String message;
}