package ru.alfabank.entities;

import lombok.Data;

import java.util.HashMap;

public @Data
class BankInfo {
    private final HashMap<String, String> details;
}