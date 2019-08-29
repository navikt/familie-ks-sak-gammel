package no.nav.familie.ks.sak.util;

import com.fasterxml.jackson.databind.JsonNode;

public class Ressurs {
    public enum Status {
        SUKSESS, FEILET, IKKE_HENTET;
    }

    private Status status;
    private JsonNode data;
    private String melding;

    Ressurs(Status status, JsonNode data, String melding) {
        this.status = status;
        this.data = data;
        this.melding = melding;
    }

    public Status getStatus() {
        return status;
    }

    public JsonNode getData() {
        return data;
    }

    public String getMelding() {
        return melding;
    }

    public static class Builder {
        public Ressurs byggVellyketRessurs(JsonNode data) {
            return new Ressurs(Status.SUKSESS, data, "Suksess");
        }

        public Ressurs byggFeiletRessurs(String melding) {
            return new Ressurs(Status.FEILET, null, melding);
        }
    }
}
