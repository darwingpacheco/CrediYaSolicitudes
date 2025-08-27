package co.com.solicitudescrediya.api.dto;

public class UserLoanValidationDTO {
    private boolean exists;
    private Integer loanStateId;

    public UserLoanValidationDTO(boolean exists, Integer loanStateId) {
        this.exists = exists;
        this.loanStateId = loanStateId;
    }

    public boolean isExists() {
        return exists;
    }

    public Integer getLoanStateId() {
        return loanStateId;
    }
}
