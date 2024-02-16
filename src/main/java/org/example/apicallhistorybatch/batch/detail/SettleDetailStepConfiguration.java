package org.example.apicallhistorybatch.batch.detail;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.example.apicallhistorybatch.domain.ApiOrder;
import org.example.apicallhistorybatch.domain.SettleDetail;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class SettleDetailStepConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Step preSettleDetailStep(
            FlatFileItemReader<ApiOrder> preSettleDetailReader,
            PreSettleDetailWriter preSettleDetailWriter,
            ExecutionContextPromotionListener executionContextPromotionListener
    ) {
        return new StepBuilder("preSettleDetailStep", jobRepository)
                .<ApiOrder, Key>chunk(5000, platformTransactionManager)
                .reader(preSettleDetailReader)
                .processor(new PreSettleDetailProcessor())
                .writer(preSettleDetailWriter)
                .listener(executionContextPromotionListener)
                .build();
    }

    /**
     * 1. 첫 번째 Step 은 파일의 고객 + 서비스 별로 집계해서 Execution Context 안에 넣는다.
     */
    @Bean
    @StepScope
    public FlatFileItemReader<ApiOrder> preSettleDetailReader(
            @Value("#{jobParameters['targetDate']}") String targetDate
    ) {
        final String fileName = targetDate + "_api_orders.csv";

        return new FlatFileItemReaderBuilder<ApiOrder>()
                .name("preSettleDetailReader")
                .resource(new ClassPathResource("/datas/" + fileName))
                .linesToSkip(1)
                .delimited()
                .names("id", "customerId", "url", "state", "createdAt")
                .targetType(ApiOrder.class)
                .build();
    }

    /**
     * 2. 두 번째 Step 은 집계된 Execution Context 데이터를 가지고 DB 에 Write 한다.
     */
    @Bean
    public Step settleDetailStep(
          SettleDetailReader settleDetailReader,
          SettleDetailProcessor settleDetailProcessor,
          JpaItemWriter<SettleDetail> settleDetailWriter

    ) {
        return new StepBuilder("settleDetailStep", jobRepository)
                .<KeyAndCount, SettleDetail>chunk(1000, platformTransactionManager)
                .reader(settleDetailReader)
                .processor(settleDetailProcessor)
                .writer(settleDetailWriter)
                .build();
    }

    @Bean
    public ExecutionContextPromotionListener promotionListener() {
        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[]{"snapshots"});

        return listener;
    }

    @Bean
    public JpaItemWriter<SettleDetail> settleDetailWriter(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<SettleDetail>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

}
