package ru.alfabank.tests.core.rest;

import lombok.NonNull;

import java.util.Arrays;

/**
 * Created by U_M0UKA on 26.01.2017.
 */
public enum OperationType {
    MORE(">"),
    LESS("<"),
    EQUAL("=="),
    MORE_O_EQUAL(">="),
    LESS_O_EQUAL("<=");

    final String literal;
    OperationType(@NonNull String literal) {
        this.literal = literal;
    }

    public static OperationType getOperationByLiteral(@NonNull String literal) {
        return Arrays.stream(OperationType.values())
                .filter(literal::equals)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(literal));
    }
}
