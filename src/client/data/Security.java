package client.data;

import java.security.Permission;

public class Security extends SecurityManager {

    SecurityManager original;

    public Security(SecurityManager original) {
        this.original = original;
    }

    /**
     * Deny permission to exit the VM.
     */
    public void checkExit(int status) {
        //throw(new SecurityException("Not allowed"));
    }

    /**
     * Allow this security manager to be replaced, if fact, allow pretty
     * much everything.
     */
    public void checkPermission(Permission perm) {
    }

    public SecurityManager getOriginalSecurityManager() {
        return original;
    }
}
