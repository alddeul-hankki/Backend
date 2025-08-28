package com.alddeul.solsolhanhankki.notification.dto;

import java.time.OffsetDateTime;

import com.alddeul.solsolhanhankki.notification.domain.NotificationEventType;

public record NotificationEventDto(
		long userId,
		String title,
		String body,
		NotificationEventType type,
		OffsetDateTime timestamp
		) {}
