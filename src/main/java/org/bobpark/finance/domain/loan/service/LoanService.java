package org.bobpark.finance.domain.loan.service;

import java.security.Principal;

import org.bobpark.finance.domain.loan.model.CreateLoanRequest;
import org.bobpark.finance.domain.loan.model.LoanResponse;

public interface LoanService {

    LoanResponse createLoan(Principal principal, CreateLoanRequest createRequest);

}
