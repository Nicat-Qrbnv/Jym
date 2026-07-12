package com.epam.jym.crm.client.workload;

import com.epam.jym.crm.dto.TrainerWorkloadUpdateRequest;
import com.epam.jym.crm.logging.TraceLoggingConstants;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "trainer-workload", path = "/v1/trainer-workloads")
public interface TrainerWorkloadClient {

  @PostMapping
  Void acceptTrainerWorkload(
      @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorization,
      @RequestHeader(name = TraceLoggingConstants.TRACE_ID_HEADER, required = false) String traceId,
      @RequestBody TrainerWorkloadUpdateRequest request);

  @PostMapping("/batch")
  Void acceptTrainerWorkload(
      @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorization,
      @RequestHeader(name = TraceLoggingConstants.TRACE_ID_HEADER, required = false) String traceId,
      @RequestBody List<TrainerWorkloadUpdateRequest> request);
}
