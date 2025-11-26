package com.smartlogis.deliveryservice.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.smartlogis.common.utils.QuerydslSortUtils;
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
		String hubDeliveryManagerId,
		DeliveryHistoryStatus status,
		Pageable pageable
	) {
		QDeliveryHistory deliveryHistory = QDeliveryHistory.deliveryHistory;

		OrderSpecifier<?>[] orderSpecifiers = QuerydslSortUtils.toOrderSpecifiers(
			DeliveryHistory.class,
			"createdAt",
			pageable.getSort()
		);

		JPAQuery<DeliveryHistory> query = queryFactory
			.selectFrom(deliveryHistory)
			.where(
				deliveryIdEq(deliveryId),
				departureHubIdEq(departureHubId),
				destinationHubIdEq(destinationHubId),
				hubDeliveryManagerIdEq(hubDeliveryManagerId),
				statusEq(status),
				deliveryHistory.deletedAt.isNull()
			)
			.orderBy(orderSpecifiers);

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

	private BooleanExpression hubDeliveryManagerIdEq(String hubDeliveryManagerId) {
		return hubDeliveryManagerId != null ?
			QDeliveryHistory.deliveryHistory.hubDeliveryManagerId.eq(hubDeliveryManagerId) : null;
	}

	private BooleanExpression statusEq(DeliveryHistoryStatus status) {
		return status != null ? QDeliveryHistory.deliveryHistory.status.eq(status) : null;
	}
}
