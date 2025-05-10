package com.siemens.internship;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleGenericException_returnsInternalServerError() {
        Exception ex = new RuntimeException("Test exception");
        ResponseEntity<String> response = handler.handleGenericException(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        assertThat(response.getBody()).contains("Test exception");
    }

}
