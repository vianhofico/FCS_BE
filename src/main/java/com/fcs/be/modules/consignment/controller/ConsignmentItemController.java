package com.fcs.be.modules.consignment.controller;

import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.modules.consignment.dto.request.CreateConsignmentItemRequest;
import com.fcs.be.modules.consignment.dto.request.UpdateConsignmentItemStatusRequest;
import com.fcs.be.modules.consignment.dto.response.ConsignmentItemResponse;
import com.fcs.be.modules.consignment.service.interfaces.ConsignmentItemService;
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
public class ConsignmentItemController {

    private final ConsignmentItemService consignmentItemService;

    public ConsignmentItemController(ConsignmentItemService consignmentItemService) {
        this.consignmentItemService = consignmentItemService;
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<ConsignmentItemResponse>> createItem(
        @Valid @RequestBody CreateConsignmentItemRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Item created", consignmentItemService.createItem(request)));
    }

    @GetMapping("/{requestId}/item")
    public ResponseEntity<ApiResponse<ConsignmentItemResponse>> getItemByRequest(@PathVariable UUID requestId) {
        return ResponseEntity.ok(ApiResponse.ok("Fetched item", consignmentItemService.getItemByRequest(requestId)));
    }

    @PatchMapping("/items/{id}/status")
    public ResponseEntity<ApiResponse<ConsignmentItemResponse>> updateItemStatus(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateConsignmentItemStatusRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Item status updated", consignmentItemService.updateItemStatus(id, request)));
    }
}
