package org.bobpark.finance.domain.loan.model.v1;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import lombok.Builder;

import org.bobpark.finance.domain.loan.entity.Loan;
import org.bobpark.finance.domain.loan.model.LoanRepaymentHistoryResponse;
import org.bobpark.finance.domain.loan.model.LoanResponse;
import org.bobpark.finance.domain.loan.type.RepaymentType;

@Builder
public record LoanV1Response(Long id,
                             String name,
                             String description,
                             LocalDate startDate,
                             LocalDate endDate,
                             Integer repaymentDate,
                             Double interestRate,
                             RepaymentType repaymentType,
                             Long totalBalance,
                             Long repaymentCount,
                             Long endingBalance,
                             List<LoanRepaymentHistoryResponse> repaymentHistories,
                             LocalDateTime createdDate,
                             String createdBy,
                             LocalDateTime lastModifiedDate,
                             String lastModifiedBy)
    implements LoanResponse {

    public static LoanResponse of(Loan entity) {
        return of(entity, false);
    }

    public static LoanResponse of(Loan entity, boolean includeHistory) {
        return LoanV1Response.builder()
            .id(entity.getId())
            .name(entity.getName())
            .description(entity.getDescription())
            .startDate(entity.getStartDate())
            .endDate(entity.getEndDate())
            .repaymentDate(entity.getRepaymentDate())
            .interestRate(entity.getInterestRate())
            .repaymentType(entity.getRepaymentType())
            .totalBalance(entity.getTotalBalance())
            .repaymentCount(entity.getRepaymentCount())
            .endingBalance(entity.getEndingBalance())
            .repaymentHistories(
                includeHistory ?
                    entity.getRepaymentHistories().stream()
                        .map(LoanRepaymentHistoryV1Response::of)
                        .toList() : Collections.emptyList())
            .createdDate(entity.getCreatedDate())
            .createdBy(entity.getCreatedBy())
            .lastModifiedDate(entity.getLastModifiedDate())
            .lastModifiedBy(entity.getLastModifiedBy())
            .build();
    }

}
