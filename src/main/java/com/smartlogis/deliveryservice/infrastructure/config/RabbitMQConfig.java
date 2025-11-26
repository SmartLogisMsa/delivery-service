package com.smartlogis.deliveryservice.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

	public static final String DELIVERY_ROUTE_QUEUE = "smartlogis.hubroute.order.route-created.queue";
	public static final String DELIVERY_EXCHANGE = "smartlogis.hubroute.order.exchange";
	public static final String DELIVERY_ROUTE_ROUTING_KEY = "smartlogis.hubroute.order.route-created";

	public static final String DELIVERY_CREATED_QUEUE = "smartlogis.delivery.created.queue";
	public static final String DELIVERY_CREATED_EXCHANGE = "smartlogis.delivery.exchange";
	public static final String DELIVERY_CREATED_ROUTING_KEY = "smartlogis.delivery.created";

	@Bean
	public Queue deliveryRouteQueue() {
		return new Queue(DELIVERY_ROUTE_QUEUE, true);
	}

	@Bean
	public TopicExchange deliveryExchange() {
		return new TopicExchange(DELIVERY_EXCHANGE, true, false);
	}

	@Bean
	public Binding deliveryRouteBinding() {
		return BindingBuilder
			.bind(deliveryRouteQueue())
			.to(deliveryExchange())
			.with(DELIVERY_ROUTE_ROUTING_KEY);
	}

	@Bean
	public Queue deliveryCreatedQueue() {
		return new Queue(DELIVERY_CREATED_QUEUE, true);
	}

	@Bean
	public TopicExchange deliveryCreatedExchange() {
		return new TopicExchange(DELIVERY_CREATED_EXCHANGE, true, false);
	}

	@Bean
	public Binding deliveryCreatedBinding() {
		return BindingBuilder
			.bind(deliveryCreatedQueue())
			.to(deliveryCreatedExchange())
			.with(DELIVERY_CREATED_ROUTING_KEY);
	}

	@Bean
	public MessageConverter messageConverter(){
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
		ConnectionFactory connectionFactory,
		MessageConverter messageConverter
	) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setMessageConverter(messageConverter);
		return factory;
	}

}
