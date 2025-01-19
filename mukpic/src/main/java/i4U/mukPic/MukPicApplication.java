package i4U.mukPic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MukPicApplication {

	public static void main(String[] args) {
		SpringApplication.run(MukPicApplication.class, args);
	}

}
