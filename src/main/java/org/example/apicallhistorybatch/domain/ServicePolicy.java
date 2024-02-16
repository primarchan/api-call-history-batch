package org.example.apicallhistorybatch.domain;

import lombok.Getter;

import java.util.Arrays;

import static java.time.chrono.JapaneseEra.values;

@Getter
public enum ServicePolicy {
    A(1L, "/example/services/a", 10),
    B(2L, "/example/services/b", 10),
    C(3L, "/example/services/c", 10),
    D(4L, "/example/services/d", 15),
    E(5L, "/example/services/e", 15),
    F(6L, "/example/services/f", 10),
    G(7L, "/example/services/g", 10),
    H(8L, "/example/services/h", 10),
    I(9L, "/example/services/i", 10),
    J(10L, "/example/services/j", 10),
    K(11L, "/example/services/k", 10),
    L(12L, "/example/services/l", 12),
    M(13L, "/example/services/m", 12),
    N(14L, "/example/services/n", 12),
    O(15L, "/example/services/o", 10),
    P(16L, "/example/services/p", 10),
    Q(17L, "/example/services/q", 10),
    R(18L, "/example/services/r", 10),
    S(19L, "/example/services/s", 10),
    T(20L, "/example/services/t", 10),
    U(21L, "/example/services/u", 10),
    V(22L, "/example/services/v", 10),
    W(23L, "/example/services/w", 19),
    X(24L, "/example/services/x", 19),
    Y(25L, "/example/services/y", 19),
    Z(26L, "/example/services/z", 19)
    ;

    private final Long id;
    private final String url;
    private final Integer fee;

    ServicePolicy(Long id, String url, Integer fee) {
        this.id = id;
        this.url = url;
        this.fee = fee;
    }

    public static ServicePolicy findByUrl(String url) {
        return Arrays.stream(values())
                .filter(it -> it.url.equals(url))
                .findFirst()
                .orElseThrow();
    }

    public static ServicePolicy findById(Long id) {
        return Arrays.stream(values())
                .filter(it -> it.id.equals(id))
                .findFirst()
                .orElseThrow();
    }

}
