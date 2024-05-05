package org.bobpark.finance.domain.loan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.bobpark.finance.domain.loan.entity.Loan;
import org.bobpark.finance.domain.loan.repository.query.LoanQueryRepository;

public interface LoanRepository extends JpaRepository<Loan, Long>, LoanQueryRepository {
}
