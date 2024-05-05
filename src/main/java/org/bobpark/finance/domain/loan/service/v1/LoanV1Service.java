package org.bobpark.finance.domain.loan.service.v1;

import static com.google.common.base.Preconditions.*;
import static org.apache.commons.lang3.ObjectUtils.*;
import static org.bobpark.finance.domain.loan.model.v1.LoanV1Response.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Comparator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;

import org.bobpark.core.exception.NotFoundException;
import org.bobpark.core.model.common.Id;
import org.bobpark.finance.common.entity.BaseTimeEntity;
import org.bobpark.finance.domain.loan.entity.Loan;
import org.bobpark.finance.domain.loan.model.CreateLoanRequest;
import org.bobpark.finance.domain.loan.model.LoanResponse;
import org.bobpark.finance.domain.loan.model.RepayLoanRequest;
import org.bobpark.finance.domain.loan.model.v1.CreateLoanV1Request;
import org.bobpark.finance.domain.loan.model.v1.RepayLoanV1Request;
import org.bobpark.finance.domain.loan.repository.LoanRepository;
import org.bobpark.finance.domain.loan.service.LoanService;
import org.bobpark.finance.domain.loan.type.RepaymentType;
import org.bobpark.finance.domain.user.feign.client.UserFeignClient;
import org.bobpark.finance.domain.user.feign.model.UserResponse;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class LoanV1Service implements LoanService {

    private final UserFeignClient userClient;

    private final LoanRepository loanRepository;

    @Transactional
    @Override
    public LoanResponse createLoan(Principal principal, CreateLoanRequest createRequest) {

        CreateLoanV1Request createV1Request = (CreateLoanV1Request)createRequest;

        UserResponse user = getUser(principal.getName());

        Loan createdLoan =
            Loan.builder()
                .userId(user.id())
                .name(createV1Request.name())
                .description(createV1Request.description())
                .startDate(createV1Request.startDate())
                .endDate(createV1Request.endDate())
                .repaymentDate(createV1Request.repaymentDate())
                .interestRate(createV1Request.interestRate())
                .repaymentType(createRequest.repaymentType())
                .totalBalance(createRequest.totalBalance())
                .build();

        createdLoan = loanRepository.save(createdLoan);

        log.debug("created loan. (id={}, name={})", createdLoan.getId(), createdLoan.getName());

        return of(createdLoan);
    }

    @Transactional
    @Override
    public LoanResponse repayLoan(Id<Loan, Long> loanId, RepayLoanRequest repayRequest) {

        RepayLoanV1Request repayV1Request = (RepayLoanV1Request)repayRequest;

        checkArgument(isNotEmpty(repayV1Request.repaymentDate()), "repaymentDate must be provided.");

        Loan loan =
            loanRepository.findById(loanId.getValue())
                .orElseThrow(() -> new NotFoundException(loanId));

        LocalDate prevRepaymentDate =
            loan.getRepaymentHistories().stream()
                .max(Comparator.comparing(BaseTimeEntity::getCreatedDate))
                .map(item -> item.getCreatedDate().toLocalDate())
                .orElse(loan.getStartDate());

        if (loan.getRepaymentType() == RepaymentType.CUSTOM) {
            // 상환 종류가 CUSTOM 인 경우 별도로 처리

            checkArgument(isNotEmpty(repayV1Request.repayment()), "repayment must be provided.");

            loan.repay(repayV1Request.repayment(), repayRequest.repaymentDate(), prevRepaymentDate);
        } else {
            loan.repay(repayV1Request.repaymentDate(), prevRepaymentDate);
        }

        log.debug("repaid loan... (loanId={})", loan.getId());

        return of(loan);

    }

    private UserResponse getUser(String userId) {
        return userClient.getUser(userId);
    }
}
