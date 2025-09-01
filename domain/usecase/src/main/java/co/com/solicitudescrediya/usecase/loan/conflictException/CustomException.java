package co.com.solicitudescrediya.usecase.loan.conflictException;


import co.com.solicitudescrediya.model.adapterExceptionApi.ApiError;

public class CustomException extends RuntimeException {
    private final ApiError apiError;

    public CustomException(ApiError apiError) {
        super(apiError.getMessage());
        this.apiError = apiError;
    }

    public ApiError getApiError() {
        return apiError;
    }
}
