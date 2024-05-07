package org.bobpark.finance.domain.loan.entity;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import org.bobpark.finance.domain.loan.type.RepaymentType;

class LoanTest {

    @Test
    void repay() {

        Loan loan =
            Loan.builder()
                .id(1L)
                .userId(1L)
                .name("test")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(4))
                .repaymentDate(8)
                // .repaymentType(RepaymentType.LEVEL_PAYMENT)
                .repaymentType(RepaymentType.BALLOON_PAYMENT)
                // .repaymentType(RepaymentType.EQUAL_PRINCIPAL_PAYMENT)
                .repaymentCount(1L)
                .interestRate(0.016)
                .totalBalance(100_000_000L)
                .endingBalance(100_000_000L)
                .build();

        LocalDate now = LocalDate.of(2023, 12, 8);
        LocalDate prevDate = LocalDate.of(2023, 11, 8);

        loan.createRepayment(0, now, prevDate);

        System.out.println(loan.getRepaymentHistories().get(0).getInterest());
    }
}