package org.bobpark.finance.domain.loan.entity;

import static com.google.common.base.Preconditions.*;
import static org.apache.commons.lang3.ObjectUtils.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.ToString.Exclude;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.FinanceLib;

import org.bobpark.finance.common.entity.BaseEntity;
import org.bobpark.finance.domain.loan.type.LoanStatus;
import org.bobpark.finance.domain.loan.type.RepaymentType;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "loans")
public class Loan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer repaymentDate;
    private Double interestRate;

    @Enumerated(EnumType.STRING)
    private RepaymentType repaymentType;

    private Long totalBalance;
    private Long repaymentCount;
    private Long endingBalance;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    private Long defaultRepaymentBalance;

    @Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanRepaymentHistory> repaymentHistories = new ArrayList<>();

    @Builder
    private Loan(Long id, Long userId, String name, String description, LocalDate startDate, LocalDate endDate,
        Integer repaymentDate, Double interestRate, RepaymentType repaymentType, Long totalBalance,
        Long repaymentCount, Long endingBalance, LoanStatus status, Long defaultRepaymentBalance) {

        checkArgument(isNotEmpty(userId), "userId must be provided.");
        checkArgument(StringUtils.isNotBlank(name), "name must be provided.");

        checkArgument(isNotEmpty(startDate), "startDate must be provided.");
        checkArgument(isNotEmpty(endDate), "endDate must be provided.");
        checkArgument(startDate.isBefore(endDate), "endDate must be grater than startDate.");

        checkArgument(isNotEmpty(repaymentDate), "repaymentDate must be provided.");

        checkArgument(isNotEmpty(repaymentType), "remainingType must be provided.");
        checkArgument(isNotEmpty(totalBalance), "totalBalance must be provided.");

        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.repaymentDate = repaymentDate;
        this.interestRate = defaultIfNull(interestRate, 0.0);
        this.repaymentType = repaymentType;
        this.totalBalance = totalBalance;
        this.repaymentCount = defaultIfNull(repaymentCount, 0L);
        this.endingBalance = defaultIfNull(endingBalance, totalBalance);
        this.status = defaultIfNull(status, LoanStatus.PROCEEDING);
        this.defaultRepaymentBalance = defaultRepaymentBalance;
    }

    /**
     * 대출 상환 메서드
     *
     * @param repayment         상환 급액 - type 이 CUSTOM 인 경우에만 적용
     * @param now               상환 날짜
     * @param prevRepaymentDate 이전 상환 날짜
     */
    public LoanRepaymentHistory createRepayment(long repayment, LocalDate now, LocalDate prevRepaymentDate) {

        checkArgument(isNotEmpty(now), "now must be provided.");
        checkArgument(isNotEmpty(prevRepaymentDate), "prevRepaymentDate must be provided.");

        long principal = 0;
        long interest = 0;

        long totalMonth = Math.abs(ChronoUnit.MONTHS.between(getStartDate(), getEndDate()));
        long days = Math.abs(ChronoUnit.DAYS.between(now, prevRepaymentDate));

        switch (getRepaymentType()) {

            case LEVEL_PAYMENT -> {
                // 원리금 균등 상환
                long total =
                    Math.round(FinanceLib.pmt(
                        getInterestRate() / 12,
                        totalMonth,
                        getTotalBalance() * -1,
                        0,
                        false));

                interest = Math.round((getEndingBalance() * getInterestRate()) / 12);
                principal = total - interest;

            }

            case EQUAL_PRINCIPAL_PAYMENT -> {
                // 원금 균등 상환

                principal = Math.round(getTotalBalance() / (double)totalMonth);

                long totalInterest =
                    Math.round((getTotalBalance() - (principal * getRepaymentCount()))
                        * getInterestRate());

                interest = Math.round(totalInterest * (days / (double)365));
            }

            case BALLOON_PAYMENT -> {
                // 만기 일시
                interest = Math.round(
                    (getTotalBalance() * getInterestRate()) * (days / (double)365));

            }

            default -> {
                // custom
                interest = Math.round((getEndingBalance() * getInterestRate()) * (days / (double)365));
                principal = repayment - interest;
            }
        }

        LoanRepaymentHistory repaymentHistory =
            LoanRepaymentHistory.builder()
                .principal(principal)
                .interest(interest)
                .round((int)(getRepaymentCount() + 1))
                .build();

        repaymentHistory.updateLoan(this);

        getRepaymentHistories().add(repaymentHistory);

        return repaymentHistory;
    }

    public void updateEndingBalance(long endingBalance) {
        this.endingBalance = endingBalance;
    }

    public void updateRepaymentCount(long repaymentCount) {
        this.repaymentCount = repaymentCount;
    }

}
