package org.bobpark.finance.domain.loan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.bobpark.finance.domain.loan.entity.LoanRepaymentHistory;

public interface LoanRepaymentHistoryRepository extends JpaRepository<LoanRepaymentHistory, Long> {
}
