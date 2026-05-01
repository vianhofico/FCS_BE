package com.fcs.be.modules.order.repository;

import com.fcs.be.modules.order.dto.request.OrderFilterRequest;
import com.fcs.be.modules.order.entity.Order;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {

    private OrderSpecification() {}

    public static Specification<Order> from(OrderFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isFalse(root.get("isDeleted")));

            if (filter.orderCode() != null && !filter.orderCode().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("orderCode")),
                    "%" + filter.orderCode().toLowerCase() + "%"));
            }

            if (filter.buyerId() != null) {
                predicates.add(cb.equal(root.get("buyer").get("id"), filter.buyerId()));
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
