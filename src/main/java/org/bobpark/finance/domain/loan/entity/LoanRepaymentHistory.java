package org.bobpark.finance.domain.loan.entity;

import static com.google.common.base.Preconditions.*;
import static org.apache.commons.lang3.ObjectUtils.*;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.ToString.Exclude;

import org.bobpark.finance.common.entity.BaseTimeEntity;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "loans_repayment_histories")
public class LoanRepaymentHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id")
    private Loan loan;

    private Long principal;
    private Long interest;

    private Integer round;

    @Builder
    private LoanRepaymentHistory(Long id, Long principal, Long interest, Integer round) {

        checkArgument(isNotEmpty(principal), "principal must be provided.");
        checkArgument(isNotEmpty(interest), "interest must be provided.");

        this.id = id;
        this.principal = principal;
        this.interest = interest;
        this.round = defaultIfNull(round, 1);
    }

    /*
        편의 메서드
         */
    public void updateLoan(Loan loan) {
        this.loan = loan;
    }
}
