package org.example.apicallhistorybatch.batch.group;

import lombok.RequiredArgsConstructor;
import org.example.apicallhistorybatch.domain.Customer;
import org.example.apicallhistorybatch.domain.SettleGroup;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SettleGroupStepConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Step settleGroupStep(
            SettleGroupReader settleGroupReader,
            SettleGroupProcessor settleGroupProcessor,
            ItemWriter<List<SettleGroup>> settleGroupItemWriter
    ) {
        return new StepBuilder("settleGroupStep", jobRepository)
                .<Customer, List<SettleGroup>>chunk(100, platformTransactionManager)
                .reader(settleGroupReader)
                .processor(settleGroupProcessor)
                .writer(settleGroupItemWriter)
                .build();
    }

    @Bean
    public ItemWriter<List<SettleGroup>> settleGroupItemWriter(
            SettleGroupItemDBWriter settleGroupItemDBWriter,
            SettleGroupItemMailWriter settleGroupItemMailWriter
    ) {
        return new CompositeItemWriter<>(
                settleGroupItemDBWriter,
                settleGroupItemMailWriter
        );
    }

}
