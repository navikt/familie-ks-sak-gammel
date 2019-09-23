package no.nav.familie.ks.sak.app.behandling.domene.grunnlag;

import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.Barn;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.AktørArbeidYtelseUtland;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.AktørTilknytningUtland;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.OppgittUtlandsTilknytning;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.BarnehageplassStatus;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Standpunkt;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.util.DateParser;

import java.util.HashSet;

public final class SøknadTilGrunnlagMapper {
    public static OppgittUtlandsTilknytning mapUtenlandsTilknytning(no.nav.familie.ks.sak.app.grunnlag.Søknad søknad, AktørId søkerAktørId, AktørId annenPartAktørId) {
        final var tilknytningTilUtland = søknad.tilknytningTilUtland;
        final var arbeidIUtlandet = søknad.arbeidIUtlandet;
        final var utenlandskeYtelser = søknad.utenlandskeYtelser;
        final var utenlandskKontantstotte = søknad.utenlandskKontantstotte;

        final var tilknytningUtlandSet = new HashSet<AktørTilknytningUtland>();
        final var arbeidYtelseUtlandSet = new HashSet<AktørArbeidYtelseUtland>();

        tilknytningUtlandSet.add(new AktørTilknytningUtland(søkerAktørId, tilknytningTilUtland.boddEllerJobbetINorgeMinstFemAar, tilknytningTilUtland.boddEllerJobbetINorgeMinstFemAarForklaring));
        arbeidYtelseUtlandSet.add(new AktørArbeidYtelseUtland.Builder()
            .setAktørId(søkerAktørId)
            .setArbeidIUtlandet(Standpunkt.map(arbeidIUtlandet.arbeiderIUtlandetEllerKontinentalsokkel, Standpunkt.UBESVART))
            .setArbeidIUtlandetForklaring(arbeidIUtlandet.arbeiderIUtlandetEllerKontinentalsokkelForklaring)
            .setYtelseIUtlandet(Standpunkt.map(utenlandskeYtelser.mottarYtelserFraUtland, Standpunkt.UBESVART))
            .setYtelseIUtlandetForklaring(utenlandskeYtelser.mottarYtelserFraUtlandForklaring)
            .setKontantstøtteIUtlandet(Standpunkt.map(utenlandskKontantstotte.mottarKontantstotteFraUtlandet, Standpunkt.UBESVART))
            .setKontantstøtteIUtlandetForklaring(utenlandskKontantstotte.mottarKontantstotteFraUtlandetTilleggsinfo)
            .build());


        final var familieforhold = søknad.getFamilieforhold();
        if (familieforhold.getAnnenForelderFødselsnummer() != null && !familieforhold.getAnnenForelderFødselsnummer().isEmpty() && annenPartAktørId != null) {
            tilknytningUtlandSet.add(new AktørTilknytningUtland(annenPartAktørId, tilknytningTilUtland.annenForelderBoddEllerJobbetINorgeMinstFemAar, tilknytningTilUtland.annenForelderBoddEllerJobbetINorgeMinstFemAarForklaring));
            arbeidYtelseUtlandSet.add(new AktørArbeidYtelseUtland.Builder()
                .setAktørId(annenPartAktørId)
                .setArbeidIUtlandet(Standpunkt.map(arbeidIUtlandet.arbeiderAnnenForelderIUtlandet, Standpunkt.UBESVART))
                .setArbeidIUtlandetForklaring(arbeidIUtlandet.arbeiderAnnenForelderIUtlandetForklaring)
                .setYtelseIUtlandet(Standpunkt.map(utenlandskeYtelser.mottarAnnenForelderYtelserFraUtland, Standpunkt.UBESVART))
                .setYtelseIUtlandetForklaring(utenlandskeYtelser.mottarAnnenForelderYtelserFraUtlandForklaring)
                .build());
        }

        return new OppgittUtlandsTilknytning(arbeidYtelseUtlandSet, tilknytningUtlandSet);
    }

    public static Barn.Builder mapSøknadBarn(no.nav.familie.ks.sak.app.grunnlag.Søknad søknad) {
        final var builder = new Barn.Builder();
        final var mineBarn = søknad.getMineBarn();
        final var barnehageplass = søknad.barnehageplass;
        builder.setAktørId(mineBarn.getFødselsnummer())
            .setBarnehageStatus(BarnehageplassStatus.map(barnehageplass.barnBarnehageplassStatus.name()));
        switch (barnehageplass.barnBarnehageplassStatus) {
            case harBarnehageplass:
                builder.setBarnehageAntallTimer(Double.parseDouble(barnehageplass.harBarnehageplassAntallTimer))
                    .setBarnehageDato(DateParser.parseInputDatoFraSøknad(barnehageplass.harBarnehageplassDato))
                    .setBarnehageKommune(barnehageplass.harBarnehageplassKommune);
                break;
            case harSluttetIBarnehage:
                builder.setBarnehageAntallTimer(Double.parseDouble(barnehageplass.harSluttetIBarnehageAntallTimer))
                    .setBarnehageDato(DateParser.parseInputDatoFraSøknad(barnehageplass.harSluttetIBarnehageDato))
                    .setBarnehageKommune(barnehageplass.harSluttetIBarnehageKommune);
                break;
            case skalSlutteIBarnehage:
                builder.setBarnehageAntallTimer(Double.parseDouble(barnehageplass.skalSlutteIBarnehageAntallTimer))
                    .setBarnehageDato(DateParser.parseInputDatoFraSøknad(barnehageplass.skalSlutteIBarnehageDato))
                    .setBarnehageKommune(barnehageplass.skalSlutteIBarnehageKommune);
                break;
            case skalBegynneIBarnehage:
                builder.setBarnehageAntallTimer(Double.parseDouble(barnehageplass.skalBegynneIBarnehageAntallTimer))
                    .setBarnehageDato(DateParser.parseInputDatoFraSøknad(barnehageplass.skalBegynneIBarnehageDato))
                    .setBarnehageKommune(barnehageplass.skalBegynneIBarnehageKommune);
                break;
        }

        return builder;
    }
}
