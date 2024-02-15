package org.example.apicallhistorybatch.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiOrder {
    private String id;
    private Long customerId;
    private String url;
    private State state;
    private String createdAt;

    public enum State {
        SUCCESS, FAIL
    }

    public ApiOrder(String id, Long customerId, String url, State state, String createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.url = url;
        this.state = state;
        this.createdAt = createdAt;
    }

}
