package no.nav.familie.ks.sak.behandling.soknad;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnnenForelder {
    @JsonProperty("annenForelderNavn")
    public String navn;
    @JsonProperty("annenForelderPersonnummer")
    public String personnummer;
    @JsonProperty("annenForelderYrkesaktivINorgeEOSIMinstFemAar")
    public String yrkesaktivINorgeEOSIMinstFemAar;
}
