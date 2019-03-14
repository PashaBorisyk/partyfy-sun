package models.persistient;

public enum UserRegistrationState {

    CONFIRMED,EXPIRED,DUPLICATE,IN_PROGRESS;

    public boolean isInProgress(){
        return this == IN_PROGRESS;
    }

}
