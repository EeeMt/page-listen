package me.ihxq.projects.pagelisten.email;

import lombok.Builder;
import lombok.Data;

/**
 * @author xq.h
 * 2020/4/19 18:18
 **/
@Data
@Builder
public class ChangeRecord {
    private String name;
    private String description;
    private Boolean hit;
    private String operator;
    private String targetValue;
    private String currentValue;
    private String resultDescription;
    private boolean success;
}
