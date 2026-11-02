package com.codename_vp.serverside;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"JWT_SECRET=test-secret-test-secret-test-secret-1234",
		"RAWG_API_KEY=test-key"
})
class ServerSideApplicationTests {

	@Test
	void contextLoads() {
	}

}
