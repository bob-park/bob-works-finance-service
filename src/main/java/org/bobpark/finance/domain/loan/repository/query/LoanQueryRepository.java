package org.bobpark.finance.domain.loan.repository.query;

import java.util.List;

import org.bobpark.finance.domain.loan.entity.Loan;

public interface LoanQueryRepository {

    List<Loan> getAllByWaiting();

    List<Loan> getAllByProceeding();

    List<Loan> getByUserId(long userId);

}
