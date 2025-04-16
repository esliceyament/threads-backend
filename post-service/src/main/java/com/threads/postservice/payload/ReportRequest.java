package com.threads.postservice.payload;

import com.threads.postservice.enums.ReportReason;
import lombok.Data;

@Data
public class ReportRequest {
    private ReportReason reason;

    private String additionalInfo;
}
