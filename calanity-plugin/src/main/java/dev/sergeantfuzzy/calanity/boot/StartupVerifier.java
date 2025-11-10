package dev.sergeantfuzzy.calanity.boot;

import java.util.logging.Logger;

/** Basic environment sanity checks. */
public final class StartupVerifier {

    public void verify(Logger logger) {
        String version = System.getProperty("java.version");
        logger.info(() -> "Preparing Calanity on Java " + version);
    }
}
