package com.evergarden.cms.app.installer;

public interface Step<T> {
    /**
     * perform the current goal of this task it can be anything like create a new folder create a user download something
     */
    void execute();

    /**
     * Tell if everything was executed without any problem and we can purshass the next step of installation
     * @return boolean
     */
    boolean isOk();

    T getInstance();
}
