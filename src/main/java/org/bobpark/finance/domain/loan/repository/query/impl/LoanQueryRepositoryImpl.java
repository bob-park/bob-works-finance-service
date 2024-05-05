package org.bobpark.finance.domain.loan.repository.query.impl;

import static org.bobpark.finance.domain.loan.entity.QLoan.*;

import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;

import com.querydsl.jpa.impl.JPAQueryFactory;

import org.bobpark.finance.domain.loan.entity.Loan;
import org.bobpark.finance.domain.loan.repository.query.LoanQueryRepository;
import org.bobpark.finance.domain.loan.type.LoanStatus;

@RequiredArgsConstructor
public class LoanQueryRepositoryImpl implements LoanQueryRepository {

    private final JPAQueryFactory query;

    @Override
    public List<Loan> getAllByWaiting() {
        return query.selectFrom(loan)
            .where(
                loan.status.eq(LoanStatus.WAITING),
                loan.startDate.loe(LocalDate.now()))
            .fetch();
    }

    @Override
    public List<Loan> getAllByProceeding() {
        return query.selectFrom(loan)
            .where(
                loan.status.eq(LoanStatus.PROCEEDING),
                loan.startDate.loe(LocalDate.now()),
                loan.endDate.goe(LocalDate.now()))
            .orderBy(loan.startDate.asc())
            .fetch();
    }
}
