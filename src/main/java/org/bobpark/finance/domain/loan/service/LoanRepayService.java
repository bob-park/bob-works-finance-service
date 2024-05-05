package org.bobpark.finance.domain.loan.service;

import org.bobpark.core.model.common.Id;
import org.bobpark.finance.domain.loan.entity.LoanRepaymentHistory;
import org.bobpark.finance.domain.loan.model.LoanRepaymentHistoryResponse;

public interface LoanRepayService {

    LoanRepaymentHistoryResponse repay(Id<LoanRepaymentHistory, Long> repayId);

}
