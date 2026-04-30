package com.fcs.be.modules.iam.controller;

import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.modules.iam.dto.request.CreateUserAddressRequest;
import com.fcs.be.modules.iam.dto.response.UserAddressResponse;
import com.fcs.be.modules.iam.service.interfaces.UserAddressService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/iam/users")
public class UserAddressController {

    private final UserAddressService userAddressService;

    public UserAddressController(UserAddressService userAddressService) {
        this.userAddressService = userAddressService;
    }

    @GetMapping("/{userId}/addresses")
    public ResponseEntity<ApiResponse<List<UserAddressResponse>>> getAddresses(@PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.ok("Fetched user addresses", userAddressService.getAddresses(userId)));
    }

    @PostMapping("/{userId}/addresses")
    public ResponseEntity<ApiResponse<UserAddressResponse>> createAddress(@PathVariable UUID userId, @Valid @RequestBody CreateUserAddressRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Address created", userAddressService.createAddress(userId, request)));
    }

    @PutMapping("/addresses/{id}")
    public ResponseEntity<ApiResponse<UserAddressResponse>> updateAddress(@PathVariable UUID id, @Valid @RequestBody CreateUserAddressRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Address updated", userAddressService.updateAddress(id, request)));
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable UUID id) {
        userAddressService.deleteAddress(id);
        return ResponseEntity.ok(ApiResponse.ok("Address deleted"));
    }
}
