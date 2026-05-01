package com.fcs.be.modules.iam.repository;

import com.fcs.be.modules.iam.dto.request.UserFilterRequest;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.entity.UserRole;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    private UserSpecification() {}

    public static Specification<User> from(UserFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isFalse(root.get("isDeleted")));

            if (filter.keyword() != null && !filter.keyword().isBlank()) {
                String pattern = "%" + filter.keyword().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("username")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern)
                ));
            }

            if (filter.status() != null) {
                predicates.add(cb.equal(root.get("status"), filter.status()));
            }

            if (filter.role() != null && !filter.role().isBlank() && query != null) {
                Subquery<UUID> sub = query.subquery(UUID.class);
                var userRole = sub.from(UserRole.class);
                sub.select(userRole.get("user").get("id"))
                    .where(
                        cb.equal(cb.lower(userRole.get("role").get("name")), filter.role().toLowerCase()),
                        cb.isFalse(userRole.get("isDeleted"))
                    );
                predicates.add(root.get("id").in(sub));
            }

            if (query != null) {
                query.orderBy(cb.desc(root.get("createdAt")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
