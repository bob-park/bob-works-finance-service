package org.bobpark.finance.domain.loan.controller.v1;

import java.security.Principal;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.bobpark.finance.domain.loan.model.LoanResponse;
import org.bobpark.finance.domain.loan.model.v1.CreateLoanV1Request;
import org.bobpark.finance.domain.loan.service.v1.LoanV1Service;

@RequiredArgsConstructor
@RestController
@RequestMapping("v1/loan")
public class LoanV1Controller {

    private final LoanV1Service loanService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "")
    public LoanResponse createLoan(Principal principal, @RequestBody CreateLoanV1Request createRequest) {

        return loanService.createLoan(principal, createRequest);
    }

}
