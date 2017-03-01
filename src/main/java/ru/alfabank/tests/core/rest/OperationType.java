package ru.alfabank.tests.core.rest;

import lombok.NonNull;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * Created by U_M0UKA on 26.01.2017.
 */
public enum OperationType {
    MORE(">"),
    LESS("<"),
    EQUAL("=="),
    MORE_O_EQUAL(">="),
    LESS_O_EQUAL("<=");
//    END_WITH,
//    CONTAINS,
//    START_WITH

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
