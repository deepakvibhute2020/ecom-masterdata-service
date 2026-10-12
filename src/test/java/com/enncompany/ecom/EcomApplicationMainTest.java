package com.enncompany.ecom;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class EcomApplicationMainTest {

  @Test
  void test_application_main_startsWithoutError() {
    assertDoesNotThrow(() -> EcomApplication.main(new String[] {"--server.port=0"}));
  }
}
