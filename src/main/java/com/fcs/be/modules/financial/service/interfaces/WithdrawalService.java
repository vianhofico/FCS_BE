package com.fcs.be.modules.financial.service.interfaces;

import com.fcs.be.modules.financial.dto.request.CreateWithdrawalRequest;
import com.fcs.be.modules.financial.dto.request.UpdateWithdrawalStatusRequest;
import com.fcs.be.modules.financial.dto.response.WithdrawalRequestResponse;
import java.util.List;
import java.util.UUID;

public interface WithdrawalService {

    List<WithdrawalRequestResponse> getWithdrawals();

    WithdrawalRequestResponse getWithdrawal(UUID id);

    WithdrawalRequestResponse createWithdrawal(CreateWithdrawalRequest request);

    WithdrawalRequestResponse updateStatus(UUID id, UpdateWithdrawalStatusRequest request, UUID reviewerId);
}
