package org.example.apicallhistorybatch.batch.detail;

import org.example.apicallhistorybatch.domain.ApiOrder;
import org.example.apicallhistorybatch.domain.ServicePolicy;
import org.springframework.batch.item.ItemProcessor;

public class PreSettleDetailProcessor implements ItemProcessor<ApiOrder, Key> {
    @Override
    public Key process(ApiOrder item) throws Exception {
        if (item.getState() == ApiOrder.State.FAIL) {
            return null;
        }

        final Long serviceId = ServicePolicy.findByUrl(item.getUrl()).getId();

        return new Key(item.getCustomerId(), serviceId);
    }

}
