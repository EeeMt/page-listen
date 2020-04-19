package me.ihxq.projects.pagelisten.email;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xq.h
 * 2020/4/19 18:22
 **/
@Data
@Builder
public class ChangeReport {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long success;
    private long failure;
    private long total;
    private long hit;
    private List<ChangeRecord> detail;
}
