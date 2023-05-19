package com.stanislav.merci;

import com.stanislav.merci.dao.PointsAccountRepository;
import com.stanislav.merci.entity.PointsAccount;
import com.stanislav.merci.service.PointsAccountService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

        Thread t1 = new Thread(() -> {
            PointsAccount account = null;
            try {
                account = repository.findByUserId(srcPointsAccount.getUserId()).orElseThrow();
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                srcPointsAccount.setAmount(100);
                service.save(account);
            }catch (Exception e){
                Assertions.assertTrue(e.getMessage().contains("Row was updated or deleted by another transaction"));
            }

        });

        Thread t2 = new Thread(() -> service.tryChangeAmount(srcPointsAccount.getUserId(), 200));
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        final PointsAccount result = repository.findByUserId( srcPointsAccount.getUserId()).orElse(null);

        assertAll(
                () -> assertEquals(1, result.getVersion()),
                () -> verify(service, times(2)).save(any())
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
