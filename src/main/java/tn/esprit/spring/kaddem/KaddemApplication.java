package tn.esprit.spring.kaddem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class KaddemApplication {
    private static final Logger logger = LogManager.getLogger(KaddemApplication.class);

    public static void main(String[] args) {
        logger.info("Starting Kaddem application...");
        SpringApplication.run(KaddemApplication.class, args);
        logger.info("Kaddem application started successfully");
    }
}
