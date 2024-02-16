package org.example.apicallhistorybatch.batch.detail;

import java.io.Serializable;

record Key(Long customerId, Long serviceId) implements Serializable {
}
