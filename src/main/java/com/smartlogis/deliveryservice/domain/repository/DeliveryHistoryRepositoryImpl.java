package com.smartlogis.deliveryservice.domain.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.smartlogis.deliveryservice.domain.entity.DeliveryHistory;
import com.smartlogis.deliveryservice.domain.entity.DeliveryHistoryStatus;
import com.smartlogis.deliveryservice.domain.entity.QDeliveryHistory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DeliveryHistoryRepositoryImpl implements DeliveryHistoryRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<DeliveryHistory> searchDeliveryHistories(
		UUID deliveryId,
		UUID departureHubId,
		UUID destinationHubId,
		UUID hubDeliveryManagerId,
		DeliveryHistoryStatus status,
		Pageable pageable
	) {
		QDeliveryHistory deliveryHistory = QDeliveryHistory.deliveryHistory;

		JPAQuery<DeliveryHistory> query = queryFactory
			.selectFrom(deliveryHistory)
			.where(
				deliveryIdEq(deliveryId),
				departureHubIdEq(departureHubId),
				destinationHubIdEq(destinationHubId),
				hubDeliveryManagerIdEq(hubDeliveryManagerId),
				statusEq(status),
				deliveryHistory.deletedAt.isNull()
			);

		for (OrderSpecifier<?> orderSpecifier : getOrderSpecifiers(pageable)) {
			query.orderBy(orderSpecifier);
		}

		List<DeliveryHistory> content = query
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = queryFactory
			.select(deliveryHistory.count())
			.from(deliveryHistory)
			.where(
				deliveryIdEq(deliveryId),
				departureHubIdEq(departureHubId),
				destinationHubIdEq(destinationHubId),
				hubDeliveryManagerIdEq(hubDeliveryManagerId),
				statusEq(status),
				deliveryHistory.deletedAt.isNull()
			)
			.fetchOne();

		return new PageImpl<>(content, pageable, total != null ? total : 0L);
	}

	private BooleanExpression deliveryIdEq(UUID deliveryId) {
		return deliveryId != null ? QDeliveryHistory.deliveryHistory.delivery.id.eq(deliveryId) : null;
	}

	private BooleanExpression departureHubIdEq(UUID departureHubId) {
		return departureHubId != null ? QDeliveryHistory.deliveryHistory.departureHubId.eq(departureHubId) : null;
	}

	private BooleanExpression destinationHubIdEq(UUID destinationHubId) {
		return destinationHubId != null ?
			QDeliveryHistory.deliveryHistory.destinationHubId.eq(destinationHubId) : null;
	}

	private BooleanExpression hubDeliveryManagerIdEq(UUID hubDeliveryManagerId) {
		return hubDeliveryManagerId != null ?
			QDeliveryHistory.deliveryHistory.hubDeliveryManagerId.eq(hubDeliveryManagerId) : null;
	}

	private BooleanExpression statusEq(DeliveryHistoryStatus status) {
		return status != null ? QDeliveryHistory.deliveryHistory.status.eq(status) : null;
	}

	private List<OrderSpecifier<?>> getOrderSpecifiers(Pageable pageable) {
		List<OrderSpecifier<?>> orders = new ArrayList<>();
		QDeliveryHistory deliveryHistory = QDeliveryHistory.deliveryHistory;

		if (!pageable.getSort().isEmpty()) {
			pageable.getSort().forEach(order -> {
				Order direction = order.isAscending() ? Order.ASC : Order.DESC;
				switch (order.getProperty()) {
					case "createdAt" -> orders.add(new OrderSpecifier<>(direction, deliveryHistory.createdAt));
					case "updatedAt" -> orders.add(new OrderSpecifier<>(direction, deliveryHistory.updatedAt));
					case "sequence" -> orders.add(new OrderSpecifier<>(direction, deliveryHistory.sequence));
					case "status" -> orders.add(new OrderSpecifier<>(direction, deliveryHistory.status));
					default -> orders.add(new OrderSpecifier<>(direction, deliveryHistory.sequence));
				}
			});
		} else {
			orders.add(new OrderSpecifier<>(Order.ASC, deliveryHistory.sequence));
		}

		return orders;
	}
}
