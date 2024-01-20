package BimBom.DBI.Model.Dto;

import java.util.List;

public class ErrorLoginDto {
    private int code;
    private String message;
    private List<ErrorDetail> errors;

    public static class ErrorDetail {
        private String message;
        private String domain;
        private String reason;
    }
}
