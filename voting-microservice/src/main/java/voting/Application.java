package voting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import voting.annotations.Generated;

@SpringBootApplication()
@Generated // Cannot test this app wrapper, do system tests instead
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
