package org.bobpark.finance.domain.loan.model.v1;

import java.time.LocalDateTime;

import lombok.Builder;

import org.bobpark.finance.domain.loan.entity.LoanRepaymentHistory;
import org.bobpark.finance.domain.loan.model.LoanRepaymentHistoryResponse;

@Builder
public record LoanRepaymentHistoryV1Response(Long id,
                                             Long principal,
                                             Long interest,
                                             Integer round,
                                             Boolean isRepaid,
                                             LocalDateTime repaymentDate,
                                             LocalDateTime createdDate,
                                             LocalDateTime lastModifiedDate)
    implements LoanRepaymentHistoryResponse {

    public static LoanRepaymentHistoryResponse of(LoanRepaymentHistory entity) {
        return LoanRepaymentHistoryV1Response.builder()
            .id(entity.getId())
            .principal(entity.getPrincipal())
            .interest(entity.getInterest())
            .round(entity.getRound())
            .isRepaid(entity.getIsRepaid())
            .repaymentDate(entity.getRepaymentDate())
            .createdDate(entity.getCreatedDate())
            .lastModifiedDate(entity.getLastModifiedDate())
            .build();
    }
}
