package com.epam.jym.crm.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.epam.jym.crm.dto.user.UserCreateDto;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UsernameGenerationIT extends AbstractIntegrationTest {

  @Autowired private UserService userService;

  @Test
  void register_withSameFullName_appendsNumericSuffix() {
    User first = userService.register(new UserCreateDto("Uniquetest", "Namegen"));
    User second = userService.register(new UserCreateDto("Uniquetest", "Namegen"));
    User third = userService.register(new UserCreateDto("Uniquetest", "Namegen"));

    assertThat(first.getUsername()).isEqualTo("uniquetest.namegen");
    assertThat(second.getUsername()).isEqualTo("uniquetest.namegen1");
    assertThat(third.getUsername()).isEqualTo("uniquetest.namegen2");
  }

  @Test
  void register_concurrentSameName_producesUniqueUsernames() throws Exception {
    int threadCount = 5;
    CountDownLatch ready = new CountDownLatch(threadCount);
    CountDownLatch start = new CountDownLatch(1);
    ExecutorService pool = Executors.newFixedThreadPool(threadCount);

    List<Future<String>> futures = new ArrayList<>();
    for (int i = 0; i < threadCount; i++) {
      futures.add(
          pool.submit(
              () -> {
                ready.countDown();
                start.await();
                return userService.register(new UserCreateDto("Concurrent", "Usergen")).getUsername();
              }));
    }

    ready.await();
    start.countDown();

    List<String> usernames = new ArrayList<>();
    for (Future<String> f : futures) {
      usernames.add(f.get());
    }

    pool.shutdown();

    assertThat(usernames).doesNotHaveDuplicates();
    assertThat(usernames).hasSize(threadCount);
  }
}
