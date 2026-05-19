package com.fcs.be.modules.product.dto.response;

public record UploadMediaResponse(
    String fileName,
    String objectKey,
    String url,
    String contentType,
    long sizeBytes
) {}
