package com.fcs.be.modules.consignment.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignConsignmentContractRequest(
    @AssertTrue boolean acceptedTerms,
    @NotBlank @Size(max = 180) String signatureName
) {}
