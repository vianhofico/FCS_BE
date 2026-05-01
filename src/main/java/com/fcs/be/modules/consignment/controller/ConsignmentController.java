package com.fcs.be.modules.consignment.controller;

import com.fcs.be.common.enums.ConsignmentRequestStatus;
import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.consignment.dto.request.ConsignmentFilterRequest;
import com.fcs.be.modules.consignment.dto.request.CreateConsignmentRequest;
import com.fcs.be.modules.consignment.dto.request.UpdateConsignmentRequest;
import com.fcs.be.modules.consignment.dto.request.UpdateConsignmentStatusRequest;
import com.fcs.be.modules.consignment.dto.response.ConsignmentResponse;
import com.fcs.be.modules.consignment.service.interfaces.ConsignmentService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/consignments")
public class ConsignmentController {

    private final ConsignmentService consignmentService;

    public ConsignmentController(ConsignmentService consignmentService) {
        this.consignmentService = consignmentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ConsignmentResponse>>> getConsignments(
        @RequestParam(required = false) String code,
        @RequestParam(required = false) UUID consignorId,
        @RequestParam(required = false) ConsignmentRequestStatus status,
        @RequestParam(required = false) Instant startDate,
        @RequestParam(required = false) Instant endDate,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        ConsignmentFilterRequest filter = new ConsignmentFilterRequest(code, consignorId, status, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.ok("Fetched consignments", consignmentService.getConsignments(filter, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ConsignmentResponse>> getConsignment(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Fetched consignment", consignmentService.getConsignment(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ConsignmentResponse>> createConsignment(
        @Valid @RequestBody CreateConsignmentRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Consignment created", consignmentService.createConsignment(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ConsignmentResponse>> updateConsignment(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateConsignmentRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Consignment updated", consignmentService.updateConsignment(id, request)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ConsignmentResponse>> updateStatus(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateConsignmentStatusRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
            "Consignment status updated",
            consignmentService.updateStatus(id, request.status(), request.reason())
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteConsignment(@PathVariable UUID id) {
        consignmentService.deleteConsignment(id);
        return ResponseEntity.ok(ApiResponse.ok("Consignment deleted"));
    }
}

