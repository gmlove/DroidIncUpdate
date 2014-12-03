package com.comeplus.droidincupdate.updateaction;

import java.io.IOException;

public interface UpdateAction {

    String getFileToHandlePath();
    void exec() throws IOException;

}
