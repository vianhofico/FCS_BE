package com.fcs.be.modules.consignment.controller;

import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.modules.consignment.dto.request.CreateConsignmentContractRequest;
import com.fcs.be.modules.consignment.dto.request.UpdateConsignmentContractStatusRequest;
import com.fcs.be.modules.consignment.dto.response.ConsignmentContractResponse;
import com.fcs.be.modules.consignment.service.interfaces.ConsignmentContractService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/consignments")
public class ConsignmentContractController {

    private final ConsignmentContractService contractService;

    public ConsignmentContractController(ConsignmentContractService contractService) {
        this.contractService = contractService;
    }

    @PostMapping("/contracts")
    public ResponseEntity<ApiResponse<ConsignmentContractResponse>> createContract(
        @Valid @RequestBody CreateConsignmentContractRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Contract created", contractService.createContract(request)));
    }

    @GetMapping("/{requestId}/contract")
    public ResponseEntity<ApiResponse<ConsignmentContractResponse>> getContractByRequest(@PathVariable UUID requestId) {
        return ResponseEntity.ok(ApiResponse.ok("Fetched contract", contractService.getContractByRequest(requestId)));
    }

    @PatchMapping("/contracts/{id}/sign")
    public ResponseEntity<ApiResponse<ConsignmentContractResponse>> signContract(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Contract signed", contractService.signContract(id)));
    }

    @PatchMapping("/contracts/{id}/status")
    public ResponseEntity<ApiResponse<ConsignmentContractResponse>> updateContractStatus(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateConsignmentContractStatusRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Contract status updated", contractService.updateContractStatus(id, request)));
    }
}
