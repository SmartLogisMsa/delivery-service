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
import com.smartlogis.deliveryservice.domain.entity.Delivery;
import com.smartlogis.deliveryservice.domain.entity.DeliveryStatus;
import com.smartlogis.deliveryservice.domain.entity.QDelivery;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DeliveryRepositoryImpl implements DeliveryRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Delivery> searchDeliveries(
		UUID orderId,
		DeliveryStatus status,
		UUID departureHubId,
		UUID destinationHubId,
		UUID companyDeliveryManagerId,
		Pageable pageable
	) {
		QDelivery delivery = QDelivery.delivery;

		OrderSpecifier<?>[] orderSpecifiers = QuerydslSortUtils.toOrderSpecifiers(
			Delivery.class,
			"createdAt",
			pageable.getSort()
		);

		JPAQuery<Delivery> query = queryFactory
			.selectFrom(delivery)
			.where(
				orderIdEq(orderId),
				statusEq(status),
				departureHubIdEq(departureHubId),
				destinationHubIdEq(destinationHubId),
				companyDeliveryManagerIdEq(companyDeliveryManagerId),
				delivery.deletedAt.isNull()
			)
			.orderBy(orderSpecifiers);

		List<Delivery> content = query
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = queryFactory
			.select(delivery.count())
			.from(delivery)
			.where(
				orderIdEq(orderId),
				statusEq(status),
				departureHubIdEq(departureHubId),
				destinationHubIdEq(destinationHubId),
				companyDeliveryManagerIdEq(companyDeliveryManagerId),
				delivery.deletedAt.isNull()
			)
			.fetchOne();

		return new PageImpl<>(content, pageable, total != null ? total : 0L);
	}

	private BooleanExpression orderIdEq(UUID orderId) {
		return orderId != null ? QDelivery.delivery.orderId.eq(orderId) : null;
	}

	private BooleanExpression statusEq(DeliveryStatus status) {
		return status != null ? QDelivery.delivery.status.eq(status) : null;
	}

	private BooleanExpression departureHubIdEq(UUID departureHubId) {
		return departureHubId != null ? QDelivery.delivery.departureHubId.eq(departureHubId) : null;
	}

	private BooleanExpression destinationHubIdEq(UUID destinationHubId) {
		return destinationHubId != null ? QDelivery.delivery.destinationHubId.eq(destinationHubId) : null;
	}

	private BooleanExpression companyDeliveryManagerIdEq(UUID companyDeliveryManagerId) {
		return companyDeliveryManagerId != null ?
			QDelivery.delivery.companyDeliveryManagerId.eq(companyDeliveryManagerId) : null;
	}
}
