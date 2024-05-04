package org.bobpark.finance.domain.loan.type;

public enum RepaymentType {

    // 원리금 균등 분할 상환
    LEVEL_PAYMENT,

    // 원금 균등 상환
    EQUAL_PRINCIPAL_PAYMENT,

    // 만기 일시 상환
    BALLOON_PAYMENT,

    CUSTOM;

}
