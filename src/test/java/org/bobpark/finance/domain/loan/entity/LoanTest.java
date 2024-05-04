package org.bobpark.finance.domain.loan.entity;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import org.bobpark.finance.domain.loan.type.RepaymentType;

class LoanTest {

    @Test
    void repayment() {

        Loan loan =
            Loan.builder()
                .id(1L)
                .userId(1L)
                .name("test")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(10))
                .repaymentDate(1)
                .repaymentType(RepaymentType.LEVEL_PAYMENT)
                // .repaymentType(RepaymentType.BALLOON_PAYMENT)
                // .repaymentType(RepaymentType.EQUAL_PRINCIPAL_PAYMENT)
                .repaymentCount(1L)
                .interestRate(0.046)
                .totalBalance(200_000_000L)
                .endingBalance(198_684_244L)
                .build();

        LocalDate now = LocalDate.of(2024, 4, 1);
        LocalDate prevDate = LocalDate.of(2024, 3, 1);

        loan.repayment(0, now, prevDate);

        System.out.println(loan.getRepaymentHistories().get(0).getInterest());
    }
}