package com.evergarden.cms.context.user.infrastructure.controller.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnAuthUser {

  private String email;

  private String password;
}
