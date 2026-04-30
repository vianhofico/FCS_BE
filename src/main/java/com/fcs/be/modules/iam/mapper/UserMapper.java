package com.fcs.be.modules.iam.mapper;

import com.fcs.be.modules.iam.dto.response.UserSummaryResponse;
import com.fcs.be.modules.iam.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserSummaryResponse toSummary(User user) {
        return new UserSummaryResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getPhone(),
            user.getStatus()
        );
    }
}