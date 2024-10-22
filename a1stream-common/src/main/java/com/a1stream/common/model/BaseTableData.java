package com.a1stream.common.model;

import java.util.List;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseTableData<T> {

    private List<T> insertRecords;
    private List<T> removeRecords;
    private List<T> updateRecords;

    private List<T> newUpdateRecords;

    public List<T> getNewUpdateRecords() {
        return Stream.concat(insertRecords.stream(), updateRecords.stream()).toList();
    }
}