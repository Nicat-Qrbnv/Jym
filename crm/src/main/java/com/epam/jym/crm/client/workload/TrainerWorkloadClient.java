package com.epam.jym.crm.client.workload;

import com.epam.jym.crm.dto.TrainerWorkloadUpdateRequest;
import com.epam.jym.crm.logging.TraceLoggingConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "trainer-workload", path = "/v1/trainer-workloads")
public interface TrainerWorkloadClient {

  @PostMapping
  void acceptTrainerWorkload(
      @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorization,
      @RequestHeader(name = TraceLoggingConstants.TRACE_ID_HEADER, required = false) String traceId,
      @RequestBody TrainerWorkloadUpdateRequest request);
}
