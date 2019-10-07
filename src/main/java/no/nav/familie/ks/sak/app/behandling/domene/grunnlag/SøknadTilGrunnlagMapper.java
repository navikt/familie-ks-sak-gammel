package no.nav.familie.ks.sak.app.behandling.domene.grunnlag;

import no.nav.familie.ks.kontrakter.søknad.Søknad;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.Barn;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.AktørArbeidYtelseUtland;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.AktørTilknytningUtland;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.OppgittUtlandsTilknytning;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.BarnehageplassStatus;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;

import java.util.HashSet;
import java.util.Set;

public final class SøknadTilGrunnlagMapper {
    public static OppgittUtlandsTilknytning mapUtenlandsTilknytning(Søknad søknad, AktørId søkerAktørId, AktørId annenPartAktørId) {
        final var søkerFødselsnummer = søknad.getSøkerFødselsnummer();

        final var tilknytningUtlandSet = new HashSet<AktørTilknytningUtland>();
        final var arbeidYtelseUtlandSet = new HashSet<AktørArbeidYtelseUtland>();

        søknad.getOppgittUtlandsTilknytning().getAktørerTilknytningTilUtlandet().forEach(aktørTilknytningUtland -> {
            final var aktørId = aktørTilknytningUtland.getFødselsnummer().equals(søkerFødselsnummer) ? søkerAktørId : annenPartAktørId;
            tilknytningUtlandSet.add(new AktørTilknytningUtland(aktørId, aktørTilknytningUtland.getFødselsnummer(), aktørTilknytningUtland.getBoddEllerJobbetINorgeMinstFemAar(), aktørTilknytningUtland.getBoddEllerJobbetINorgeMinstFemAarForklaring()));
        });


        søknad.getOppgittUtlandsTilknytning().getAktørerArbeidYtelseIUtlandet().forEach(aktørArbeidYtelseUtland -> {
            final var aktørId = aktørArbeidYtelseUtland.getFødselsnummer().equals(søkerFødselsnummer) ? søkerAktørId : annenPartAktørId;
            arbeidYtelseUtlandSet.add(new AktørArbeidYtelseUtland.Builder()
                .setAktørId(aktørId)
                .setFnr(aktørArbeidYtelseUtland.getFødselsnummer())
                .setArbeidIUtlandet(aktørArbeidYtelseUtland.getArbeidIUtlandet())
                .setArbeidIUtlandetForklaring(aktørArbeidYtelseUtland.getArbeidIUtlandetForklaring())
                .setYtelseIUtlandet(aktørArbeidYtelseUtland.getYtelseIUtlandet())
                .setYtelseIUtlandetForklaring(aktørArbeidYtelseUtland.getYtelseIUtlandetForklaring())
                .setKontantstøtteIUtlandet(aktørArbeidYtelseUtland.getKontantstøtteIUtlandet())
                .setKontantstøtteIUtlandetForklaring(aktørArbeidYtelseUtland.getKontantstøtteIUtlandetForklaring())
                .build());
        });

        return new OppgittUtlandsTilknytning(arbeidYtelseUtlandSet, tilknytningUtlandSet);
    }

    public static Set<Barn> mapSøknadBarn(Søknad søknad) {
        Set<Barn> barna = new HashSet<>();

        final var barnaFraSøknaden = søknad.getOppgittFamilieforhold().getBarna();
        barnaFraSøknaden.forEach(barn -> {
            final var builder = new Barn.Builder();

            if (barn != null && barn.getBarnehageAntallTimer() != null) {
                builder.setAktørId(barn.getFødselsnummer())
                    .setBarnehageStatus(BarnehageplassStatus.map(barn.getBarnehageStatus().name()))
                    .setBarnehageAntallTimer(barn.getBarnehageAntallTimer())
                    .setBarnehageDato(barn.getBarnehageDato())
                    .setBarnehageKommune(barn.getBarnehageKommune());
            }

            barna.add(builder.build());
        });

        return barna;
    }
}
