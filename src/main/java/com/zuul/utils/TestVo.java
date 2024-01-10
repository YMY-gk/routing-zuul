package com.zuul.utils;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TestVo {
    private  String name;
    private  Integer id;
}
