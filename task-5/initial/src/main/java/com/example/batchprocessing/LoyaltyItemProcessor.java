package com.example.batchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class LoyaltyItemProcessor implements ItemProcessor<Loyalty, Loyalty> {

	private static final Logger log = LoggerFactory.getLogger(LoyaltyItemProcessor.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

    @Override
	public Loyalty process(final Loyalty loyalty) {
		final Long productSku = loyalty.productSku();
		final String loyaltyData = loyalty.loyaltyData();

		AtomicReference<Loyalty> transformedLoyalty = new AtomicReference<>(new Loyalty(productSku, loyaltyData));
		log.info("Transforming ({}) into ({})", loyalty, transformedLoyalty.get());
		return transformedLoyalty.get();
	}
}
