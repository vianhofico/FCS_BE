package com.fcs.be.modules.iam.service.interfaces;

import com.fcs.be.modules.iam.dto.request.CreateUserAddressRequest;
import com.fcs.be.modules.iam.dto.response.UserAddressResponse;
import java.util.List;
import java.util.UUID;

public interface UserAddressService {

    List<UserAddressResponse> getAddresses(UUID userId);

    UserAddressResponse createAddress(UUID userId, CreateUserAddressRequest request);

    UserAddressResponse updateAddress(UUID id, CreateUserAddressRequest request);

    void deleteAddress(UUID id);
}
