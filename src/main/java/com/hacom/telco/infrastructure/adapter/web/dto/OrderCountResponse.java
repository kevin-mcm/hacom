package com.hacom.telco.infrastructure.adapter.web.dto;

import java.time.OffsetDateTime;

public record OrderCountResponse(OffsetDateTime from, OffsetDateTime to, Long count) {}
