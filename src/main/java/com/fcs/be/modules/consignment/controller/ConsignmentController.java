package com.fcs.be.modules.consignment.controller;

import com.fcs.be.common.enums.ConsignmentRequestStatus;
import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.modules.consignment.dto.request.CreateConsignmentRequest;
import com.fcs.be.modules.consignment.dto.request.UpdateConsignmentRequest;
import com.fcs.be.modules.consignment.dto.request.UpdateConsignmentStatusRequest;
import com.fcs.be.modules.consignment.dto.response.ConsignmentResponse;
import com.fcs.be.modules.consignment.service.interfaces.ConsignmentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
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
    public ResponseEntity<ApiResponse<List<ConsignmentResponse>>> getConsignments(
        @RequestParam(required = false) ConsignmentRequestStatus status
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Fetched consignments", consignmentService.getConsignments(status)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ConsignmentResponse>> getConsignment(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Fetched consignment", consignmentService.getConsignment(id)));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ConsignmentResponse>> createConsignment(
        @Valid @RequestBody CreateConsignmentRequest request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Consignment created", consignmentService.createConsignment(request)));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ConsignmentResponse>> updateConsignment(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateConsignmentRequest request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Consignment updated", consignmentService.updateConsignment(id, request)));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ConsignmentResponse>> updateStatus(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateConsignmentStatusRequest request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(
                "Consignment status updated",
                consignmentService.updateStatus(id, request.status(), request.reason())
            ));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteConsignment(@PathVariable UUID id) {
        try {
            consignmentService.deleteConsignment(id);
            return ResponseEntity.ok(ApiResponse.ok("Consignment deleted"));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }
}
