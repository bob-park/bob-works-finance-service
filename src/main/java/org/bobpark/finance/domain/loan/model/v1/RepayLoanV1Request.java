package org.bobpark.finance.domain.loan.model.v1;

import java.time.LocalDate;

import org.bobpark.finance.domain.loan.model.RepayLoanRequest;

public record RepayLoanV1Request(Long repayment,
                                 LocalDate repaymentDate)
    implements RepayLoanRequest {
}
