package org.bobpark.finance.domain.loan.service.v1;

import static org.bobpark.finance.domain.loan.model.v1.LoanRepaymentHistoryV1Response.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.bobpark.core.exception.NotFoundException;
import org.bobpark.core.model.common.Id;
import org.bobpark.finance.domain.loan.entity.LoanRepaymentHistory;
import org.bobpark.finance.domain.loan.model.LoanRepaymentHistoryResponse;
import org.bobpark.finance.domain.loan.repository.LoanRepaymentHistoryRepository;
import org.bobpark.finance.domain.loan.service.LoanRepayService;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class LoanRepayV1Service implements LoanRepayService {

    private final LoanRepaymentHistoryRepository repaymentRepository;

    @Transactional
    @Override
    public LoanRepaymentHistoryResponse repay(Id<LoanRepaymentHistory, Long> repayId) {

        LoanRepaymentHistory repayment =
            repaymentRepository.findById(repayId.getValue())
                .orElseThrow(() -> new NotFoundException(repayId));

        repayment.completeRepayment();

        return of(repayment);
    }
}
