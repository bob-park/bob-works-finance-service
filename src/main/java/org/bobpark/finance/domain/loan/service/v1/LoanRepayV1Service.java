package org.bobpark.finance.domain.loan.service.v1;

import static org.bobpark.finance.domain.loan.model.v1.LoanRepaymentHistoryV1Response.*;

import java.text.MessageFormat;
import java.util.List;

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
import org.bobpark.finance.domain.user.feign.client.UserFeignClient;
import org.bobpark.finance.domain.user.feign.model.SendUserNotificationRequest;
import org.bobpark.finance.domain.user.feign.model.UserResponse;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class LoanRepayV1Service implements LoanRepayService {

    private static final String MESSAGE_FORMAT = "%s님 \"%s\" 대출 (*%d* 회차) 납부 금액 *%s원* 납부 완료되었습니다.";

    private final UserFeignClient userClient;

    private final LoanRepaymentHistoryRepository repaymentRepository;

    @Transactional
    @Override
    public LoanRepaymentHistoryResponse repay(Id<LoanRepaymentHistory, Long> repayId) {

        LoanRepaymentHistory repayment =
            repaymentRepository.findById(repayId.getValue())
                .orElseThrow(() -> new NotFoundException(repayId));

        repayment.completeRepayment();

        log.debug("repaid loan. (loanId={}, repaymentId={})", repayment.getLoan().getId(), repayment.getId());

        List<UserResponse> users = userClient.getUsers(List.of(repayment.getLoan().getUserId()));
        UserResponse user = findUser(users, repayment.getLoan().getUserId());

        if (user != null) {
            userClient.sendNotification(
                user.id(),
                SendUserNotificationRequest.builder()
                    .message(generateMessage(user, repayment))
                    .build());
        }

        return of(repayment);
    }

    private String generateMessage(UserResponse user, LoanRepaymentHistory repayment) {
        return String.format(MESSAGE_FORMAT,
            user.name(),
            repayment.getLoan().getName(),
            repayment.getRound(),
            MessageFormat.format("{0}", repayment.getPrincipal() + repayment.getInterest()));
    }

    private List<UserResponse> getUsers(List<Long> ids) {
        return userClient.getUsers(ids);
    }

    private UserResponse findUser(List<UserResponse> users, long userId) {
        return users.stream()
            .filter(user -> user.id().equals(userId))
            .findAny()
            .orElse(null);
    }
}
