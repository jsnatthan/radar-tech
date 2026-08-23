package com.radartech.dto;

import com.radartech.domain.AppUser;

public record GoogleUserProvision(AppUser user, boolean created) {
}
