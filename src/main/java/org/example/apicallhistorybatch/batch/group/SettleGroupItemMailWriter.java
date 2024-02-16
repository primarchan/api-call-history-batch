package org.example.apicallhistorybatch.batch.group;

import org.example.apicallhistorybatch.batch.support.EmailProvider;
import org.example.apicallhistorybatch.domain.Customer;
import org.example.apicallhistorybatch.domain.ServicePolicy;
import org.example.apicallhistorybatch.domain.SettleGroup;
import org.example.apicallhistorybatch.domain.repository.CustomerRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SettleGroupItemMailWriter implements ItemWriter<List<SettleGroup>> {
    private final CustomerRepository customerRepository;
    private final EmailProvider emailProvider;

    public SettleGroupItemMailWriter() {
        this.customerRepository = new CustomerRepository.Fake();
        this.emailProvider = new EmailProvider.Fake();
    }

    //유료 API 총 사용회수, 총 요금
    // 세부사항에 대해서 (url, 몇건, 얼마)
    @Override
    public void write(Chunk<? extends List<SettleGroup>> chunk) throws Exception {
        for (List<SettleGroup> settleGroups : chunk) {
            if (settleGroups.isEmpty())
                continue;

            final SettleGroup settleGroup = settleGroups.get(0);
            final Long customerId = settleGroup.getCustomerId();

            final Customer customer = customerRepository.findById(customerId);
            final Long totalCount = settleGroups.stream().map(SettleGroup::getTotalCount).reduce(0L, Long::sum);
            final Long totalFee = settleGroups.stream().map(SettleGroup::getTotalFee).reduce(0L, Long::sum);
            final List<String> detailByService = settleGroups
                    .stream()
                    .map(it ->
                            "\n\"%s\" - 총 사용수 : %s, 총 비용 : %s".formatted(
                                    ServicePolicy.findById(it.getServiceId()).getUrl(),
                                    it.getTotalCount(),
                                    it.getTotalFee()
                            )
                    )
                    .toList();


            final String body = """
                    안녕하세요. %s 고객님. 사용하신 유료 API 과금안내 드립니다.
                    총 %s건을 사용하셨으며, %s원의 비용이 발생했습니다.
                    세부내역은 다음과 같습니다. 감사합니다.
                    %s
                    """.formatted(
                    customer.getName(),
                    totalCount,
                    totalFee,
                    detailByService
            );

            emailProvider.send(customer.getEmail(), "유료 API 과금 안내", body);

        }
    }

}
