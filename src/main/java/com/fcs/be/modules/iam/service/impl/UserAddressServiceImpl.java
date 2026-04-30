package com.fcs.be.modules.iam.service.impl;

import com.fcs.be.modules.iam.dto.request.CreateUserAddressRequest;
import com.fcs.be.modules.iam.dto.response.UserAddressResponse;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.entity.UserAddress;
import com.fcs.be.modules.iam.mapper.UserAddressMapper;
import com.fcs.be.modules.iam.repository.UserAddressRepository;
import com.fcs.be.modules.iam.repository.UserRepository;
import com.fcs.be.modules.iam.service.interfaces.UserAddressService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressRepository userAddressRepository;
    private final UserRepository userRepository;
    private final UserAddressMapper userAddressMapper;

    public UserAddressServiceImpl(
        UserAddressRepository userAddressRepository,
        UserRepository userRepository,
        UserAddressMapper userAddressMapper
    ) {
        this.userAddressRepository = userAddressRepository;
        this.userRepository = userRepository;
        this.userAddressMapper = userAddressMapper;
    }

    @Override
    public List<UserAddressResponse> getAddresses(UUID userId) {
        return userAddressRepository.findByUserIdAndIsDeletedFalseOrderByIsDefaultDesc(userId).stream()
            .map(userAddressMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional
    public UserAddressResponse createAddress(UUID userId, CreateUserAddressRequest request) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (request.isDefault()) {
            userAddressRepository.clearDefaultAddresses(userId);
        }

        UserAddress address = new UserAddress();
        address.setUser(user);
        address.setFullName(request.fullName());
        address.setPhone(request.phone());
        address.setStreet(request.street());
        address.setWard(request.ward());
        address.setDistrict(request.district());
        address.setCity(request.city());
        address.setDefault(request.isDefault());
        address.setType(request.type());

        return userAddressMapper.toResponse(userAddressRepository.save(address));
    }

    @Override
    @Transactional
    public UserAddressResponse updateAddress(UUID id, CreateUserAddressRequest request) {
        UserAddress address = userAddressRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        if (request.isDefault() && !address.isDefault()) {
            userAddressRepository.clearDefaultAddresses(address.getUser().getId());
        }

        address.setFullName(request.fullName());
        address.setPhone(request.phone());
        address.setStreet(request.street());
        address.setWard(request.ward());
        address.setDistrict(request.district());
        address.setCity(request.city());
        address.setDefault(request.isDefault());
        address.setType(request.type());

        return userAddressMapper.toResponse(userAddressRepository.save(address));
    }

    @Override
    @Transactional
    public void deleteAddress(UUID id) {
        UserAddress address = userAddressRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Address not found"));
        address.setDeleted(true);
        userAddressRepository.save(address);
    }
}
