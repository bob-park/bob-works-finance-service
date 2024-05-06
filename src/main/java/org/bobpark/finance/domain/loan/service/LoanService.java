package org.bobpark.finance.domain.loan.service;

import java.util.List;

import org.bobpark.core.model.common.Id;
import org.bobpark.finance.domain.loan.entity.Loan;
import org.bobpark.finance.domain.loan.model.CreateLoanRequest;
import org.bobpark.finance.domain.loan.model.LoanResponse;

public interface LoanService {

    LoanResponse createLoan(CreateLoanRequest createRequest);

    List<LoanResponse> getAllByCurrentUser();

    LoanResponse getById(Id<Loan, Long> id);
}
