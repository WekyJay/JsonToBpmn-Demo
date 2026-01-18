package cn.wekyjay.test.condition;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 筛选条件
 */
@Data
public class Condition implements Serializable {
    private String group;
    private String type;
    private String symbol;
    private List<String> name;
    private String valueType;
    private String compare;
    private Object compareVal;
}
