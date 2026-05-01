package com.fcs.be.modules.return_request.service.interfaces;

import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.return_request.dto.request.CreateReturnRequestRequest;
import com.fcs.be.modules.return_request.dto.request.ReturnFilterRequest;
import com.fcs.be.modules.return_request.dto.request.UpdateReturnStatusRequest;
import com.fcs.be.modules.return_request.dto.response.ReturnRequestResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface ReturnRequestService {

    PageResponse<ReturnRequestResponse> getReturnRequests(ReturnFilterRequest filter, Pageable pageable);

    ReturnRequestResponse getReturnRequest(UUID id);

    ReturnRequestResponse createReturnRequest(CreateReturnRequestRequest request, UUID requestedById);

    ReturnRequestResponse updateStatus(UUID id, UpdateReturnStatusRequest request, UUID reviewerId);
}
