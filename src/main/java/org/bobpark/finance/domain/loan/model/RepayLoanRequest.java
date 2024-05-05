package org.bobpark.finance.domain.loan.model;

import java.time.LocalDate;

public interface RepayLoanRequest {

    Long repayment();

    LocalDate repaymentDate();

}
