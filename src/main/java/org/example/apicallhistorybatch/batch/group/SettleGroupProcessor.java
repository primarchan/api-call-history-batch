package org.example.apicallhistorybatch.batch.group;

import lombok.RequiredArgsConstructor;
import org.example.apicallhistorybatch.domain.Customer;
import org.example.apicallhistorybatch.domain.SettleGroup;
import org.example.apicallhistorybatch.domain.repository.SettleGroupRepository;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SettleGroupProcessor implements ItemProcessor<Customer, List<SettleGroup>>, StepExecutionListener {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final SettleGroupRepository settleGroupRepository;
    private StepExecution stepExecution;

    @Override
    public List<SettleGroup> process(Customer item) throws Exception {
        final String targetDate = stepExecution.getJobParameters().getString("targetDate");
        final LocalDate end = LocalDate.parse(targetDate, formatter);


        return settleGroupRepository.findGroupByCustomerIdAndServiceId(
                end.minusDays(6),
                end,
                item.getId()
        );
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

}
