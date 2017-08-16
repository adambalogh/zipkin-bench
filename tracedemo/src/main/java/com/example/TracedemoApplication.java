package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
@RestController
public class TracedemoApplication {
    private static final Logger LOG = Logger.getLogger(TracedemoApplication.class.getName());

    public static void main(String[] args) {
        SpringApplication.run(TracedemoApplication.class, args);
    }

    @Autowired private RestTemplate restTemplate;

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @RequestMapping("/")
    public String index() throws InterruptedException {
        System.out.println("Sleeping " + Thread.currentThread().getId());
        Thread.sleep(4000);
        return "Hello World";
    }

    @RequestMapping("/work")
    public Trace callHome() {
        Trace trace = new Trace();
        trace.add("Frontend at: " + System.currentTimeMillis());
        trace = restTemplate.postForObject("http://localhost:8090", trace, Trace.class);
        trace.add("Frontend at: " + System.currentTimeMillis());
        trace = restTemplate.postForObject("http://localhost:8090", trace, Trace.class);
        return trace;
    }

    @Bean
    public AlwaysSampler defaultSampler() {
        return new AlwaysSampler();
    }
}
