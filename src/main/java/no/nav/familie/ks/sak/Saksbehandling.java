package no.nav.familie.ks.sak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.familie.ks.sak.app.behandling.*;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.Barn;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.OppgittFamilieforhold;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.BarnehageplassStatus;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Standpunkt;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.resultat.UtfallType;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.grunnlag.Oppslag;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.util.DateParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.util.Set;

@Service
public class Saksbehandling {

    private VurderSamletTjeneste vurderSamletTjeneste;
    private PeriodeOppretter periodeOppretter = new PeriodeOppretter();
    private ObjectMapper mapper;
    private Oppslag oppslag;

    @Autowired
    public Saksbehandling(Oppslag oppslag, VurderSamletTjeneste vurderSamletTjeneste, ObjectMapper objectMapper) {
        this.oppslag = oppslag;
        this.vurderSamletTjeneste = vurderSamletTjeneste;
        this.mapper = objectMapper;
    }

    public Vedtak behandle(String søknadJson, String personident) {
        Søknad søknad = tilSøknad(søknadJson);
        mapTilDommene(søknad);
        Faktagrunnlag faktagrunnlag = fastsettFakta(søknad, personident);
        SamletVilkårsVurdering vilkårvurdering = vurderVilkår(faktagrunnlag);
        Vedtak vedtak = fattVedtak(vilkårvurdering, faktagrunnlag);
        return vedtak;
    }

    private void mapTilDommene(Søknad søknad) {
        final var søknadBarnBuilder = mapSøknadBarn(søknad);

        final var familieforholdBuilder = new OppgittFamilieforhold.Builder();
        familieforholdBuilder.setBarna(Set.of(søknadBarnBuilder.build()));
        familieforholdBuilder.setBorBeggeForeldreSammen(Standpunkt.map(søknad.familieforhold.borForeldreneSammenMedBarnet).equals(Standpunkt.JA));

        new Søknad();
    }

    private Barn.Builder mapSøknadBarn(Søknad søknad) {
        final var builder = new Barn.Builder();
        final var mineBarn = søknad.getMineBarn();
        final var barnehageplass = søknad.barnehageplass;
        builder.setAktørId(mineBarn.getFødselsnummer())
                .setBarnehageStatus(BarnehageplassStatus.map(barnehageplass.barnBarnehageplassStatus.name()));
        switch (barnehageplass.barnBarnehageplassStatus) {
            case harBarnehageplass:
                builder.setBarnehageAntallTimer(Integer.parseInt(barnehageplass.harBarnehageplassAntallTimer))
                        .setBarnehageDato(DateParser.parseSøknadDato(barnehageplass.harBarnehageplassDato))
                        .setBarnehageKommune(barnehageplass.harBarnehageplassKommune);
                break;
            case harSluttetIBarnehage:
                builder.setBarnehageAntallTimer(Integer.parseInt(barnehageplass.harSluttetIBarnehageAntallTimer))
                        .setBarnehageDato(DateParser.parseSøknadDato(barnehageplass.harSluttetIBarnehageDato))
                        .setBarnehageKommune(barnehageplass.harSluttetIBarnehageKommune);
                break;
            case skalSlutteIBarnehage:
                builder.setBarnehageAntallTimer(Integer.parseInt(barnehageplass.skalSlutteIBarnehageAntallTimer))
                        .setBarnehageDato(DateParser.parseSøknadDato(barnehageplass.skalSlutteIBarnehageDato))
                        .setBarnehageKommune(barnehageplass.skalSlutteIBarnehageKommune);
                break;
            case skalBegynneIBarnehage:
                builder.setBarnehageAntallTimer(Integer.parseInt(barnehageplass.skalBegynneIBarnehageAntallTimer))
                        .setBarnehageDato(DateParser.parseSøknadDato(barnehageplass.skalBegynneIBarnehageDato))
                        .setBarnehageKommune(barnehageplass.skalBegynneIBarnehageKommune);
                break;
        }

        return builder;
    }

    public Vedtak behandle(Søknad søknad, String personident) {
        Faktagrunnlag faktagrunnlag = fastsettFakta(søknad, personident);
        SamletVilkårsVurdering vilkårvurdering = vurderVilkår(faktagrunnlag);
        Vedtak vedtak = fattVedtak(vilkårvurdering, faktagrunnlag);
        return vedtak;
    }

    private Faktagrunnlag fastsettFakta(Søknad søknad, String personident) {
        // søknadsdata, TPS-data og evt. barnehagelister
        TpsFakta tpsFakta = oppslag.hentTpsFakta(søknad, personident);
        return new Faktagrunnlag.Builder()
                .medTpsFakta(tpsFakta)
                .medSøknad(søknad)
                .build();
    }

    private SamletVilkårsVurdering vurderVilkår(Faktagrunnlag grunnlag) {
        return vurderSamletTjeneste.vurder(grunnlag);
    }

    private GradertPeriode fastsettPeriode(Faktagrunnlag grunnlag) {
        return periodeOppretter.opprettStønadPeriode(grunnlag);
    }

    private Vedtak fattVedtak(SamletVilkårsVurdering vilkårvurdering, Faktagrunnlag faktagrunnlag) {
        UtfallType utfallType = vilkårvurdering.getUtfallType();
        switch (utfallType) {
            case IKKE_OPPFYLT:
                return new Vedtak(vilkårvurdering);
            case OPPFYLT:
                GradertPeriode stønadperiode = fastsettPeriode(faktagrunnlag);
                return new Vedtak(vilkårvurdering, stønadperiode);
            default:
                throw new UnsupportedOperationException(String.format("Ukjent utfalltype: %s", utfallType.name()));
        }
    }

    private String toJson(Faktagrunnlag grunnlag) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(grunnlag);
        } catch (JsonProcessingException e) {
            throw new VilkårRegelFeil("Kunne ikke serialisere regelinput for avklaring av uttaksperioder.", e);
        }
    }

    private Søknad tilSøknad(String json) {
        try {
            return mapper.readValue(new File(json), Søknad.class);
        } catch (IOException e) {
            throw new IOError(e);
        }
    }
}
