package com.tsolution.base.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GuestActionDTO {
    private boolean isProcessLogin;
    private boolean isProcessRegister;
    private boolean isActiveAccount;
    private boolean isDismiss;
    private String userName;
    private String password;
}
