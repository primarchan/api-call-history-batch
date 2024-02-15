package org.example.apicallhistorybatch.batch.generator;

import lombok.RequiredArgsConstructor;
import org.example.apicallhistorybatch.domain.ApiOrder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;
import org.springframework.transaction.PlatformTransactionManager;

// @Configuration
@RequiredArgsConstructor
public class ApiOrderGenerateJobConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job apiOrderGeneratorJob(Step step) {
        return new JobBuilder("apiOrderGeneratorJob", jobRepository)
                .start(step)
                .incrementer(new RunIdIncrementer())
                .validator(new DefaultJobParametersValidator(new String[]{"totalCount", "targetDate"}, new String[0]))
                .build();
    }

    @Bean
    public Step apiOrderGeneratorStep(
            ApiOrderGenerateReader apiOrderGenerateReader,
            ApiOrderGenerateProcessor apiOrderGenerateProcessor
    ) {
        return new StepBuilder("apiOrderGeneratorStep", jobRepository)
                .<Boolean, ApiOrder>chunk(5000, platformTransactionManager)
                .reader(apiOrderGenerateReader)
                .processor(apiOrderGenerateProcessor)
                .writer(apiOrderGenerateWriter(null))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<ApiOrder> apiOrderGenerateWriter(
            @Value("#{jobParameters['targetDate']}") String targetDate
    ) {
        final String fileName = targetDate + "_api_orders.csv";

        return new FlatFileItemWriterBuilder<ApiOrder>()
                .name("apiOrderGenerateWriter")
                .resource(new PathResource("src/main/resources/datas/" + fileName))
                .delimited()
                .names("id", "customerId", "url", "state", "createdAt")
                .headerCallback(writer -> writer.write("id,customerId,url,state,createdAt"))
                .build();
    }


}
