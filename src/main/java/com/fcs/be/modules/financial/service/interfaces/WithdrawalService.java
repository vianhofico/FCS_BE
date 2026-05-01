package com.fcs.be.modules.financial.service.interfaces;

import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.financial.dto.request.CreateWithdrawalRequest;
import com.fcs.be.modules.financial.dto.request.UpdateWithdrawalStatusRequest;
import com.fcs.be.modules.financial.dto.request.WithdrawalFilterRequest;
import com.fcs.be.modules.financial.dto.response.WithdrawalRequestResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface WithdrawalService {

    PageResponse<WithdrawalRequestResponse> getWithdrawals(WithdrawalFilterRequest filter, Pageable pageable);

    WithdrawalRequestResponse getWithdrawal(UUID id);

    WithdrawalRequestResponse createWithdrawal(CreateWithdrawalRequest request);

    WithdrawalRequestResponse updateStatus(UUID id, UpdateWithdrawalStatusRequest request, UUID reviewerId);
}
