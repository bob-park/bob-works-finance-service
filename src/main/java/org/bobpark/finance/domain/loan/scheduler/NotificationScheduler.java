package org.bobpark.finance.domain.loan.scheduler;

import static org.apache.commons.lang3.ObjectUtils.*;

import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cglib.core.Local;
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
import org.bobpark.finance.domain.loan.repository.LoanRepository;
import org.bobpark.finance.domain.user.feign.client.UserFeignClient;
import org.bobpark.finance.domain.user.feign.model.SendUserNotificationRequest;
import org.bobpark.finance.domain.user.feign.model.UserResponse;

@Slf4j
@RequiredArgsConstructor
@Component
@Transactional(readOnly = true)
public class NotificationScheduler {

    private static final String MESSAGE_USER_NOTIFICATION = "%s님 금일(%d-%02d-%02d) \"%s\" 대출 원금 및 이자 상환일입니다.";

    private final BobWorksOAuth2Properties properties;

    private final BobWorksOAuth2FeignClient authClient;
    private final UserFeignClient userClient;

    private final LoanRepository loanRepository;

    @Scheduled(cron = "${bob-works.cron-change-status:0 10 0 * * *}")
    public void changeStatus() {
        // loanRepository.
    }

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

            userClient.sendNotification(
                user.id(),
                SendUserNotificationRequest.builder()
                    .message(
                        String.format(
                            MESSAGE_USER_NOTIFICATION,
                            user.name(),
                            now.getYear(),
                            now.getMonthValue(),
                            now.getDayOfMonth(),
                            loan.getName()))
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

}
