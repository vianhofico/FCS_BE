package com.fcs.be.modules.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateOrderTrackingRequest(
    @NotBlank(message = "Tracking number is required")
    @Size(max = 100, message = "Tracking number must be at most 100 characters")
    String trackingNumber,

    @NotBlank(message = "Shipping provider is required")
    @Size(max = 100, message = "Shipping provider must be at most 100 characters")
    String shippingProvider
) {}
