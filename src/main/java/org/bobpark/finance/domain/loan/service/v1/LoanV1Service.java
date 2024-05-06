package org.bobpark.finance.domain.loan.service.v1;

import static org.bobpark.finance.domain.loan.model.v1.LoanV1Response.*;

import java.security.Principal;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.bobpark.core.exception.NotFoundException;
import org.bobpark.core.model.common.Id;
import org.bobpark.finance.domain.loan.entity.Loan;
import org.bobpark.finance.domain.loan.model.CreateLoanRequest;
import org.bobpark.finance.domain.loan.model.LoanResponse;
import org.bobpark.finance.domain.loan.model.v1.CreateLoanV1Request;
import org.bobpark.finance.domain.loan.model.v1.LoanV1Response;
import org.bobpark.finance.domain.loan.repository.LoanRepository;
import org.bobpark.finance.domain.loan.service.LoanService;
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
    public LoanResponse createLoan(CreateLoanRequest createRequest) {

        CreateLoanV1Request createV1Request = (CreateLoanV1Request)createRequest;

        UserResponse user = getUser(getUsername());

        Loan createdLoan =
            Loan.builder()
                .userId(user.id())
                .name(createV1Request.name())
                .description(createV1Request.description())
                .startDate(createV1Request.startDate())
                .endDate(createV1Request.endDate())
                .repaymentDate(createV1Request.repaymentDate())
                .interestRate(createV1Request.interestRate())
                .repaymentType(createV1Request.repaymentType())
                .totalBalance(createV1Request.totalBalance())
                .defaultRepaymentBalance(createV1Request.defaultRepaymentBalance())
                .build();

        createdLoan = loanRepository.save(createdLoan);

        log.debug("created loan. (id={}, name={})", createdLoan.getId(), createdLoan.getName());

        return of(createdLoan);
    }

    @Override
    public List<LoanResponse> getAllByCurrentUser() {

        UserResponse user = getUser(getUsername());

        List<Loan> result = loanRepository.getByUserId(user.id());

        return result.stream()
            .map(LoanV1Response::of)
            .toList();
    }

    @Override
    public LoanResponse getById(Id<Loan, Long> id) {

        Loan loan =
            loanRepository.findById(id.getValue())
                .orElseThrow(() -> new NotFoundException(id));

        return of(loan, true);
    }

    private String getUsername() {
        JwtAuthenticationToken auth =
            (JwtAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();

        return auth.getName();
    }

    private UserResponse getUser(String userId) {
        return userClient.getUser(userId);
    }
}
