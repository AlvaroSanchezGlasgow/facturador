package com.modules.invoicer.user.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void getFullNameConcatenatesNames() {
        User user = User.builder().firstName("John").lastName("Doe").build();
        assertThat(user.getFullName()).isEqualTo("John Doe");
    }

    @Test
    void getFullNameHandlesNulls() {
        User user = new User();
        assertThat(user.getFullName()).isEqualTo("");
    }
}
