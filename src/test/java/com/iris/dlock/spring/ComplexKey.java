package com.iris.dlock.spring;

import lombok.Data;

import java.util.List;

/**
 * Created by chenjinlong on 17/4/26.
 */
@Data
public class ComplexKey {
    private List<String> list;

    private String value;
}
