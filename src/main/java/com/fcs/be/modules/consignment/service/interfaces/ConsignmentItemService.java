package com.fcs.be.modules.consignment.service.interfaces;

import com.fcs.be.modules.consignment.dto.request.CreateConsignmentItemRequest;
import com.fcs.be.modules.consignment.dto.request.UpdateConsignmentItemStatusRequest;
import com.fcs.be.modules.consignment.dto.response.ConsignmentItemResponse;
import java.util.UUID;

public interface ConsignmentItemService {

    ConsignmentItemResponse createItem(CreateConsignmentItemRequest request);

    ConsignmentItemResponse getItemByRequest(UUID requestId);

    ConsignmentItemResponse updateItemStatus(UUID id, UpdateConsignmentItemStatusRequest request);
}
