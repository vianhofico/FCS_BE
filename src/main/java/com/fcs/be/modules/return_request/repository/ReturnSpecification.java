package com.fcs.be.modules.return_request.repository;

import com.fcs.be.modules.return_request.dto.request.ReturnFilterRequest;
import com.fcs.be.modules.return_request.entity.ReturnRequest;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class ReturnSpecification {

    private ReturnSpecification() {}

    public static Specification<ReturnRequest> from(ReturnFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isFalse(root.get("isDeleted")));

            if (filter.orderId() != null) {
                predicates.add(cb.equal(root.get("order").get("id"), filter.orderId()));
            }

            if (filter.requestedById() != null) {
                predicates.add(cb.equal(root.get("requestedBy").get("id"), filter.requestedById()));
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
