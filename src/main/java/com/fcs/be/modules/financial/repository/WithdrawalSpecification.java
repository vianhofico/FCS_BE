package com.fcs.be.modules.financial.repository;

import com.fcs.be.modules.financial.dto.request.WithdrawalFilterRequest;
import com.fcs.be.modules.financial.entity.WithdrawalRequest;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class WithdrawalSpecification {

    private WithdrawalSpecification() {}

    public static Specification<WithdrawalRequest> from(WithdrawalFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isFalse(root.get("isDeleted")));

            if (filter.walletId() != null) {
                predicates.add(cb.equal(root.get("wallet").get("id"), filter.walletId()));
            }

            if (filter.status() != null) {
                predicates.add(cb.equal(root.get("status"), filter.status()));
            }

            if (filter.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.startDate()));
            }

            if (filter.endDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.endDate()));
            }

            if (query != null) {
                query.orderBy(cb.desc(root.get("createdAt")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
