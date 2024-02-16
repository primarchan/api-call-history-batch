package org.example.apicallhistorybatch.batch;

import lombok.RequiredArgsConstructor;
import org.example.apicallhistorybatch.batch.support.DateFormatJobParametersValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SettleJobConfiguration {

    private final JobRepository jobRepository;

    @Bean
    public Job settleJob(
            Step preSettleDetailStep,
            Step settleDetailStep
    ) {
        return new JobBuilder("settleJob", jobRepository)
                .validator(new DateFormatJobParametersValidator(new String[]{"targetDate"}))
                .start(preSettleDetailStep)
                .next(settleDetailStep)
                .build();
    }

}
