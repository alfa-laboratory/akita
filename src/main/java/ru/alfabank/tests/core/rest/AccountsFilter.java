package ru.alfabank.tests.core.rest;

/**
 * Created by U_M0UKA on 18.01.2017.
 */
public class AccountsFilter {
    private String name;
    private String value;
    private OperationType type;

    public AccountsFilter(String name, String value, String type) throws Exception {
            this.name = name;
            this.value = value;
            setEqualType(type);

    }

    private void setEqualType(String type){
        if (type.equals("==")){
            this.type = OperationType.EQUAL;
        }
    }

    private void setMoreType(String type){
        if (type.equals(">")){
            this.type = OperationType.MORE;
        }
    }

    private void setMoreOrEqualType(String type){
        if (type.equals(">=")){
            this.type = OperationType.MORE_O_EQUAL;
        }
    }

    private void setLessOrEqualType(String type){
        if (type.equals("<=")){
            this.type = OperationType.MORE;
        }
    }

    public String getNameOfParameter() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
