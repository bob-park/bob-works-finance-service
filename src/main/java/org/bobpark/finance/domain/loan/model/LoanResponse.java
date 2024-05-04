package org.bobpark.finance.domain.loan.model;

import java.time.LocalDate;

import org.bobpark.finance.domain.loan.type.RepaymentType;

public interface LoanResponse {

    Long id();

    String name();

    String description();

    LocalDate startDate();

    LocalDate endDate();

    Integer repaymentDate();

    Double interestRate();

    RepaymentType repaymentType();

    Long totalBalance();

    Long repaymentCount();

    Long endingBalance();

}
