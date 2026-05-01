package com.fcs.be.modules.consignment.repository;

import com.fcs.be.modules.consignment.dto.request.ConsignmentFilterRequest;
import com.fcs.be.modules.consignment.entity.ConsignmentRequest;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class ConsignmentSpecification {

    private ConsignmentSpecification() {}

    public static Specification<ConsignmentRequest> from(ConsignmentFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isFalse(root.get("isDeleted")));

            if (filter.code() != null && !filter.code().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("code")),
                    "%" + filter.code().toLowerCase() + "%"));
            }

            if (filter.consignorId() != null) {
                predicates.add(cb.equal(root.get("consignor").get("id"), filter.consignorId()));
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
