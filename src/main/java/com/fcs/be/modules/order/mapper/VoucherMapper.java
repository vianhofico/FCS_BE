package com.fcs.be.modules.order.mapper;

import com.fcs.be.modules.order.dto.response.VoucherResponse;
import com.fcs.be.modules.order.entity.Voucher;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VoucherMapper {

    VoucherResponse toResponse(Voucher voucher);
}