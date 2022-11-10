package uz.pdp.appjwtrealemailauditing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {

    private String message;
    private boolean succes;
    private Object object;

    public ApiResponse(String message, boolean succes) {
        this.message = message;
        this.succes = succes;
    }
}
