package com.threads.postservice.payload;

import com.threads.postservice.enums.ReportReason;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReportRequest {
    private ReportReason reason;

    @Size(max = 50)
    private String additionalInfo;
}
