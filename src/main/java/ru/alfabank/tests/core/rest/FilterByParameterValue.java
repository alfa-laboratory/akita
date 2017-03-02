package ru.alfabank.tests.core.rest;

import lombok.NonNull;

/**
 * Created by U_M0UKA on 18.01.2017.
 */
public class FilterByParameterValue {

    // TODO needs to be final
    private String name;
    private String value;
    private OperationType type;

    public FilterByParameterValue(String name, String value, String type) throws Exception {
            this.name = name;
            this.value = value;
            this.type = OperationType.getOperationByLiteral(type);

    }

    @Deprecated
    private void setEqualType(@NonNull String type){
        if (type.equals("==")){
            this.type = OperationType.EQUAL;
        }
    }

    @Deprecated
    private void setMoreType(@NonNull String type){
        if (type.equals(">")){
            this.type = OperationType.MORE;
        }
    }

    @Deprecated
    private void setMoreOrEqualType(@NonNull String type){
        if (type.equals(">=")){
            this.type = OperationType.MORE_O_EQUAL;
        }
    }

    @Deprecated
    private void setLessOrEqualType(@NonNull String type){
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
