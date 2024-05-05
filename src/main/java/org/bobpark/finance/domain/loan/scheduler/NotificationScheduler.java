package org.bobpark.finance.domain.loan.scheduler;

import static org.apache.commons.lang3.ObjectUtils.*;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.lang3.StringUtils;

import org.bobpark.finance.common.auth.BobWorksAuthenticationContextHolder;
import org.bobpark.finance.configure.oauth2.propreties.BobWorksOAuth2Properties;
import org.bobpark.finance.domain.auth.feign.client.BobWorksOAuth2FeignClient;
import org.bobpark.finance.domain.auth.feign.model.AuthTokenRequest;
import org.bobpark.finance.domain.auth.feign.model.AuthTokenResponse;
import org.bobpark.finance.domain.loan.entity.Loan;
import org.bobpark.finance.domain.loan.entity.LoanRepaymentHistory;
import org.bobpark.finance.domain.loan.repository.LoanRepaymentHistoryRepository;
import org.bobpark.finance.domain.loan.repository.LoanRepository;
import org.bobpark.finance.domain.loan.type.RepaymentType;
import org.bobpark.finance.domain.user.feign.client.UserFeignClient;
import org.bobpark.finance.domain.user.feign.model.SendUserNotificationRequest;
import org.bobpark.finance.domain.user.feign.model.UserResponse;

@Slf4j
@RequiredArgsConstructor
@Component
@Transactional(readOnly = true)
public class NotificationScheduler {

    private static final String MESSAGE_USER_NOTIFICATION = "%s님 금일(%d-%02d-%02d) \"%s\" 대출 원금 및 이자 상환일입니다. \n\n\t대출 납부 아이디:\t %d\n\t원금: \t*%s원*\n\t이자: \t*%s원*\n\t납부 총액: \t*%s원*";

    private final BobWorksOAuth2Properties properties;

    private final BobWorksOAuth2FeignClient authClient;
    private final UserFeignClient userClient;

    private final LoanRepository loanRepository;
    private final LoanRepaymentHistoryRepository repaymentRepository;

    @Scheduled(cron = "${bob-works.cron-change-status:0 10 0 * * *}")
    public void changeStatus() {
        // loanRepository.
    }

    @Transactional
    @Scheduled(cron = "${bob-works.cron-send-notification:0 0 9 * * *}")
    public void sendNotification() {

        List<Loan> loans = loanRepository.getAllByProceeding();

        if (loans.isEmpty()) {
            log.warn("empty notification loans...");
            return;
        }

        login();

        List<Long> ids =
            loans.stream()
                .map(Loan::getUserId)
                .toList();

        List<UserResponse> users = getUsers(ids);

        LocalDate now = LocalDate.now();

        for (Loan loan : loans) {

            LocalDate repaymentDate = LocalDate.of(now.getYear(), now.getMonthValue(), loan.getRepaymentDate());
            UserResponse user = findUser(users, loan.getUserId());

            if (isEmpty(user)) {
                log.warn("no exist user info.");
                continue;
            }

            if (!repaymentDate.equals(now)) {
                log.debug("no repay loan day. (now={}, repaymentDate={})", now, repaymentDate);
                continue;
            }

            // 납부 금액 생성
            long defaultPaymentBalance =
                loan.getRepaymentType() == RepaymentType.CUSTOM ? loan.getDefaultRepaymentBalance() : 0;

            LocalDate prevRepaymentDate =
                loan.getRepaymentHistories().stream()
                    .filter(LoanRepaymentHistory::getIsRepaid)
                    .max(Comparator.comparing(LoanRepaymentHistory::getRepaymentDate))
                    .map(item -> item.getRepaymentDate().toLocalDate())
                    .orElse(loan.getStartDate());

            LoanRepaymentHistory createdRepayment = loan.createRepayment(defaultPaymentBalance, now, prevRepaymentDate);

            createdRepayment = repaymentRepository.save(createdRepayment);

            userClient.sendNotification(
                user.id(),
                SendUserNotificationRequest.builder()
                    .message(
                        generateMessage(createdRepayment, user, repaymentDate))
                    .build());

        }

        revokeAccessToken();

    }

    private void login() {

        AuthTokenRequest request =
            AuthTokenRequest.builder()
                .clientId(properties.clientId())
                .clientSecret(properties.clientSecret())
                .grantType("client_credentials")
                .scope(StringUtils.join(properties.scopes(), " "))
                .build();

        AuthTokenResponse response = authClient.token(request);

        BobWorksAuthenticationContextHolder.setContext(response.accessToken());
    }

    private void revokeAccessToken() {
        BobWorksAuthenticationContextHolder.setContext(null);
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

    private String generateMessage(LoanRepaymentHistory repayment, UserResponse user, LocalDate repaymentDate) {

        return String.format(
            MESSAGE_USER_NOTIFICATION,
            user.name(),
            repaymentDate.getYear(),
            repaymentDate.getMonthValue(),
            repaymentDate.getDayOfMonth(),
            repayment.getLoan().getName(),
            repayment.getId(),
            MessageFormat.format("{0}", repayment.getPrincipal()),
            MessageFormat.format("{0}", repayment.getInterest()),
            MessageFormat.format("{0}", repayment.getPrincipal() + repayment.getInterest()));
    }

}
