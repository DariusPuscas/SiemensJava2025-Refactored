package com.siemens.internship;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InternshipApplicationTests {

	@Autowired
	private ItemController itemController;

	@Autowired
	private ItemService itemService;

	@Test
	void contextLoads() {
		assertThat(itemController).isNotNull();
		assertThat(itemService).isNotNull();
	}
}
