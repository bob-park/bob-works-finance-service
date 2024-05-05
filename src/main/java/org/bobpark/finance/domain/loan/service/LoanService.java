package org.bobpark.finance.domain.loan.service;

import java.security.Principal;

import org.bobpark.core.model.common.Id;
import org.bobpark.finance.domain.loan.entity.Loan;
import org.bobpark.finance.domain.loan.model.CreateLoanRequest;
import org.bobpark.finance.domain.loan.model.LoanResponse;
import org.bobpark.finance.domain.loan.model.RepayLoanRequest;

public interface LoanService {

    LoanResponse createLoan(Principal principal, CreateLoanRequest createRequest);

    LoanResponse repayLoan(Id<Loan, Long> loanId, RepayLoanRequest repayRequest);

}
