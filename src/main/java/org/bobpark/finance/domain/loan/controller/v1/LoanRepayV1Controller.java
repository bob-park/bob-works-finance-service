package org.bobpark.finance.domain.loan.controller.v1;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.bobpark.core.model.common.Id;
import org.bobpark.finance.domain.loan.entity.LoanRepaymentHistory;
import org.bobpark.finance.domain.loan.model.LoanRepaymentHistoryResponse;
import org.bobpark.finance.domain.loan.service.v1.LoanRepayV1Service;

@RequiredArgsConstructor
@RestController
@RequestMapping("v1/loan/repay")
public class LoanRepayV1Controller {

    private final LoanRepayV1Service repayService;

    @PostMapping(path = "{repayId:\\d+}")
    public LoanRepaymentHistoryResponse repay(@PathVariable long repayId) {
        return repayService.repay(Id.of(LoanRepaymentHistory.class, repayId));
    }
}
