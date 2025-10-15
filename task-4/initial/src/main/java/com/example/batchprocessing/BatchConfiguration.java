package com.example.batchprocessing;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
public class BatchConfiguration {

    @Bean
        public FlatFileItemReader<Loyalty> loyaltyReader() {
        	return new FlatFileItemReaderBuilder<Loyalty>()
        		.name("loyaltyItemReader")
        		.resource(new ClassPathResource("loyality_data.csv"))
        		.delimited()
        		.names("productSku", "loyaltyData")
        		.targetType(Loyalty.class)
        		.build();
        }
    	
    	@Bean
        public LoyaltyItemProcessor loyaltyProcessor() {
        	return new LoyaltyItemProcessor();
        }
    	
    	@Bean
        public JdbcBatchItemWriter<Loyalty> loyaltyWriter(DataSource dataSource) {
        	return new JdbcBatchItemWriterBuilder<Loyalty>()
        		.sql("INSERT INTO loyalty_data(productSku, loyaltyData) " +
        				"VALUES (:productSku, :loyaltyData)")
        		.dataSource(dataSource)
        		.beanMapped()
        		.build();
        }
  

        @Bean
        public Step loyaltyStep(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
        				  FlatFileItemReader<Loyalty> reader, LoyaltyItemProcessor loyaltyProcessor, JdbcBatchItemWriter<Loyalty> writer) {
        	return new StepBuilder("loyaltyStep", jobRepository)
        		.<Loyalty, Loyalty>chunk(3, transactionManager)
        		.reader(reader)
        		.processor(loyaltyProcessor)
        		.writer(writer)
        		.build();
        }


	@Bean
	public FlatFileItemReader<Product> productReader() {
		return new FlatFileItemReaderBuilder<Product>()
			.name("productItemReader")
			.resource(new ClassPathResource("product-data.csv"))
			.delimited()
			.names("productId", "productSku","productName", "productAmount", "productData")
			.targetType(Product.class)
			.build();
	}

	@Bean
	public ProductItemProcessor productProcessor() {
		return new ProductItemProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<Product> productWriter(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Product>()
			.sql("INSERT INTO products (productId, productSku, productName, productAmount, productData) " +
					"VALUES (:productId, :productSku, :productName, :productAmount, :productData)")
			.dataSource(dataSource)
			.beanMapped()
			.build();
	}
	
	@Bean
    public Job importProductJob(JobRepository jobRepository, Step loyaltyStep, Step productStep, JobCompletionNotificationListener listener) {
    	return new JobBuilder("importProductJob", jobRepository)
    		.listener(listener)
    		.start(loyaltyStep)
    		.next(productStep)
    		.build();
    }

	@Bean
	public Step productStep(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
					  FlatFileItemReader<Product> reader, ProductItemProcessor productProcessor, JdbcBatchItemWriter<Product> writer) {
		return new StepBuilder("productStep", jobRepository)
			.<Product, Product>chunk(3, transactionManager)
			.reader(reader)
			.processor(productProcessor)
			.writer(writer)
			.build();
	}

}
