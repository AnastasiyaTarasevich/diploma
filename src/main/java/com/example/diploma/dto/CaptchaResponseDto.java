package com.example.diploma.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.Set;


@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CaptchaResponseDto {
    /**
     * Successful status from response.
     */
    private boolean success;

    /**
     * Errors from response.
     * The @JsonAlias annotation used to define one or more alternative names for property,
     * accepted during deserialization as alternative to the official name.
     */
    @JsonAlias("error-codes")
    private Set<String> errorCodes;

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setErrorCodes(Set<String> errorCodes) {
        this.errorCodes = errorCodes;
    }
}