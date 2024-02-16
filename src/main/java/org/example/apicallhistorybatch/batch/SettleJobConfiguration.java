package org.example.apicallhistorybatch.batch;

import lombok.RequiredArgsConstructor;
import org.example.apicallhistorybatch.batch.support.DateFormatJobParametersValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
@RequiredArgsConstructor
public class SettleJobConfiguration {

    private final JobRepository jobRepository;

    /**
     * 1. 일일 정산 배치
     * 2. 1주일 간 데이터 집계
     * 3. DB 에 적재 후 고객사에 Email 전송
     */
    @Bean
    public Job settleJob(
            Step preSettleDetailStep,
            Step settleDetailStep,
            Step settleGroupStep
    ) {
        return new JobBuilder("settleJob", jobRepository)
                .validator(new DateFormatJobParametersValidator(new String[]{"targetDate"}))
                .start(preSettleDetailStep)
                .next(settleDetailStep)
                .next(isFridayDecider())
                .on("COMPLETED").to(settleGroupStep).build()
                .build();
    }

    /**
     * 매주 금요일 마다 주간 정산 실행
     */
    public JobExecutionDecider isFridayDecider() {
        return (jobExecution, stepExecution) -> {
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            final String targetDate = stepExecution.getJobParameters().getString("targetDate");
            final LocalDate date = LocalDate.parse(targetDate, formatter);

            if (date.getDayOfWeek() != DayOfWeek.FRIDAY) {
                return new FlowExecutionStatus("NOOP");
            }

            return FlowExecutionStatus.COMPLETED;
        };
    }

}
