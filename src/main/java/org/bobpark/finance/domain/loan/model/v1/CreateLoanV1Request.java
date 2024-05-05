package org.bobpark.finance.domain.loan.model.v1;

import java.time.LocalDate;

import org.bobpark.finance.domain.loan.model.CreateLoanRequest;
import org.bobpark.finance.domain.loan.type.RepaymentType;

public record CreateLoanV1Request(String name,
                                  String description,
                                  LocalDate startDate,
                                  LocalDate endDate,
                                  Integer repaymentDate,
                                  Double interestRate,
                                  RepaymentType repaymentType,
                                  Long totalBalance,
                                  Long defaultRepaymentBalance)
    implements CreateLoanRequest {
}
