package com.jobtracker.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FunnelStatsResponse {

    private long totalApplications;
    private long applied;
    private long screening;
    private long interview;
    private long offer;
    private long rejected;
    private long withdrawn;
    private double offerRate;
    private double rejectionRate;
}