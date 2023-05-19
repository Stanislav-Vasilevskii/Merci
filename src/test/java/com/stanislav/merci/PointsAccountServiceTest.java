package com.stanislav.merci;

import com.stanislav.merci.dao.PointsAccountRepository;
import com.stanislav.merci.entity.PointsAccount;
import com.stanislav.merci.service.PointsAccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class PointsAccountServiceTest {
    @SpyBean
    private PointsAccountService service;

    @Autowired
    private PointsAccountRepository repository;

    private final List<Integer> amounts = Arrays.asList(100, 50, -30);

    @Test
    void shouldChangeAmount_withoutConcurrency() {
        // given
        final PointsAccount srcPointsAccount = repository.save( preparePointsAccount());
        assertEquals(0, srcPointsAccount.getVersion());

        // when
        for (final int amount : amounts) {
            service.tryChangeAmount(srcPointsAccount.getUserId(), amount);
        }

        // then
        final PointsAccount pointsAccount = repository
                .findById(srcPointsAccount.getId()).orElseThrow(() -> new IllegalArgumentException("No item found!"));

        assertAll(
                () -> assertEquals(3, pointsAccount.getVersion()),
                () -> assertEquals(120, pointsAccount.getAmount()),
                () -> verify(service, times(3)).tryChangeAmount(any(), anyInt())
        );
    }

    @Test
    void shouldChangeAmount_withOptimisticLockingHandling() throws InterruptedException {
        // given
        final PointsAccount srcPointsAccount = repository.save( preparePointsAccount());
        assertEquals(0, srcPointsAccount.getVersion());

        List<Callable<PointsAccount>> tasks;
        List<PointsAccount> canceledTasks = null;

        // when
        try (ExecutorService executor = Executors.newFixedThreadPool(amounts.size())) {

            tasks = amounts.stream()
                    .map(amount -> ((Callable<PointsAccount>)(() -> service.tryChangeAmount(srcPointsAccount.getUserId(), amount))))
                    .toList();

            try {
                canceledTasks = executor.invokeAll(tasks).stream()
                        .map(task -> {
                            try{
                                return task.get();
                            } catch (Exception ex){
                                return null;
                            }
                        })
                        .toList();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            executor.shutdown();
            assertTrue(executor.awaitTermination(1, TimeUnit.MINUTES));
        }

        // then
        final PointsAccount pointsAccount = repository
                .findById(srcPointsAccount.getId()).orElseThrow(() -> new IllegalArgumentException("No item found!"));

        List<PointsAccount> finalCanceledTasks = canceledTasks;
        assertAll(
                () -> assertEquals(1, pointsAccount.getVersion()),
                () -> {
                    assert finalCanceledTasks != null;
                    assertEquals(2, finalCanceledTasks.size());
                },
                () -> verify(service, times(3)).tryChangeAmount(any(), anyInt())
        );
    }

    private static PointsAccount preparePointsAccount(){
        PointsAccount account = new PointsAccount();
        account.setUserId(UUID.randomUUID());
        account.setVersion(0L);
        account.setAmount(0);
        return account;
    }
}
