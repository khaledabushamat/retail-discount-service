package io.github.khaledabushamat.discount;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class RetailDiscountServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
