package com.bahalla.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Flux;

import java.util.Map;

@Configuration
public class AppConfiguration {

    @Bean
    public ApplicationRunner geography(ReactiveRedisTemplate<String, String> template) {
        return args -> {

            var sicily = "Sicily";
            var geoTemplate = template.opsForGeo();
            var mapOfPoint = Map.of(
                    "Casablanca", new Point(33.5731, 7.5898),
                    "Marrakesh", new Point(31.6295, 7.9811),
                    "Paris", new Point(48.8566, 2.3522));

            Flux.fromIterable(mapOfPoint.entrySet())
                    .flatMap(e -> geoTemplate.add(sicily, e.getValue(), e.getKey()))
                    .thenMany(geoTemplate.radius(sicily, new Circle(
                            new Point(33.5731, 7.589),
                            new Distance(10, RedisGeoCommands.DistanceUnit.KILOMETERS)
                    )))
                    .map(GeoResult::getContent)
                    .map(RedisGeoCommands.GeoLocation::getName)
                    .doOnNext(System.out::println)
                    .subscribe();

        };
    }

    @Bean
    public ApplicationRunner list(ReactiveRedisTemplate<String, String> template) {
        return args -> {

            var templateList = template.opsForList();
            var listName = "fruits";

            var push = templateList.leftPushAll(listName, "Banana", "orange", "Apple");

            push
                    .thenMany(templateList.leftPop(listName))
                    .doOnNext(s -> System.out.println(s))
                    .thenMany(templateList.leftPop(listName))
                    .doOnNext(s -> System.out.println(s))
                    .subscribe();
        };
    }


    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {

        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("10.0.0.9", 6379));
    }
}
