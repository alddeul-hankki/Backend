package com.alddeul.solsolhanhankki.global.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    // 설정 값을 클래스 상수로 정의
    private static final int CONNECT_TIMEOUT_MILLIS = 5000;
    private static final int READ_TIMEOUT_SECONDS = 5;
    private static final int MAX_IN_MEMORY_SIZE = 2 * 1024 * 1024; // 2MB

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MILLIS)
                .doOnConnected(connection ->
                        connection.addHandlerLast(new ReadTimeoutHandler(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)));

        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .codecs(config -> config.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE))
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}

//    @Bean
//    public ConnectionProvider connectionProvider() {
//        //데이터가 전달되기 전 커넥션 끊김 방지
//        return ConnectionProvider.builder("http-pool")
//                .maxConnections(100)
//                .pendingAcquireTimeout(Duration.ofMillis(0))
//                .pendingAcquireMaxCount(-1)
//                .maxIdleTime(Duration.ofMillis(1000L))
//                .build();
//    }
//}