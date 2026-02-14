package com.ibrahim.DBPulse;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Basic smoke test to verify the application context loads successfully.
 */
class DbPulseApplicationTests extends IntegrationTestBase {

	@Test
	void contextLoads() {
		// This test ensures the Spring context loads successfully
		assertThat(true).isTrue();
	}

	@Test
	void postgresContainerIsRunning() {
		assertThat(postgres.isRunning()).isTrue();
		assertThat(postgres.getDatabaseName()).isEqualTo("testdb");
	}
}
