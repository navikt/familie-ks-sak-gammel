package no.nav.familie.ks.sak.util;

import org.slf4j.LoggerFactory;

public class SporingsLoggHelper {
    private static final char SPACE_SEPARATOR = ' ';
    private static final String SPORING_LOG = "sporing";

    // Pure helper, no instance
    private SporingsLoggHelper() {
    }

    public static void logSporing(Class<?> clazz, Sporingsdata sporingsdata, String actionType, String action) {
        StringBuilder msg = new StringBuilder()
            .append("action=").append(action).append(SPACE_SEPARATOR)
            .append("actionType=").append(actionType).append(SPACE_SEPARATOR);
        for (SporingsloggId id : sporingsdata.getNøkler()) {
            msg.append(id.getSporingsloggKode()).append('=').append(sporingsdata.getVerdi(id)).append(SPACE_SEPARATOR);
        }
        String sanitizedMsg =msg.toString().replaceAll("(\\r|\\n)", "");
        LoggerFactory.getLogger(SPORING_LOG + "." + clazz.getName()).info(sanitizedMsg);
    }

    public static void logSporing(Class<?> clazz, String saksnummer, String saksbehandler, String actionType, String action) {
        Sporingsdata sporingsdata = Sporingsdata.opprett();
        sporingsdata.leggTilId(SporingsloggId.ANSVALIG_SAKSBEHANDLER, saksbehandler);
        sporingsdata.leggTilId(SporingsloggId.SAKSNUMMER, saksnummer);
        logSporing(clazz, sporingsdata, actionType,action);
    }
}
