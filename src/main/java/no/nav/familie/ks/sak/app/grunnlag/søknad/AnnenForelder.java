package no.nav.familie.ks.sak.app.grunnlag.søknad;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnnenForelder {
    @JsonProperty("annenForelderNavn")
    public String navn;
    @JsonProperty("annenForelderPersonnummer")
    public String personnummer;
    @JsonProperty("annenForelderYrkesaktivINorgeEOSIMinstFemAar")
    public String yrkesaktivINorgeEOSIMinstFemAar;
}
