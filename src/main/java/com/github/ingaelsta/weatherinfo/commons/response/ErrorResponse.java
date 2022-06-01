package com.github.ingaelsta.weatherinfo.commons.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
public class ErrorResponse {
    private HttpStatus status;
    private List<String> messages;

    public ErrorResponse(HttpStatus status, List<String> messages) {
        this.status = status;
        this.messages = messages;
    }
}






