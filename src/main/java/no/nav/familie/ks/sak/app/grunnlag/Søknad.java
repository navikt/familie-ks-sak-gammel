package no.nav.familie.ks.sak.app.grunnlag;

import no.nav.familie.ks.sak.app.grunnlag.søknad.*;

import java.time.Instant;

import static java.time.Instant.now;

// FIXME: fields bør være immutable..
public class Søknad {

    public Veiledning veiledning;
    public Instant innsendingsTidspunkt;
    public Person person;
    public SokerKrav kravTilSoker;
    public Familieforhold familieforhold;
    public Barnehageplass barnehageplass;
    public ArbeidIUtlandet arbeidIUtlandet;
    public UtenlandskKontantstotte utenlandskKontantstotte;
    public TilknytningTilUtland tilknytningTilUtland;
    public UtenlandskeYtelser utenlandskeYtelser;
    public Oppsummering oppsummering;
    public String sprak;
    private Barn mineBarn;

    public Søknad() {
        this.veiledning = new Veiledning();
        this.person = new Person();
        this.kravTilSoker = new SokerKrav();
        this.familieforhold = new Familieforhold();
        this.barnehageplass = new Barnehageplass();
        this.arbeidIUtlandet = new ArbeidIUtlandet();
        this.utenlandskeYtelser = new UtenlandskeYtelser();
        this.oppsummering = new Oppsummering();
        this.utenlandskKontantstotte = new UtenlandskKontantstotte();
        this.mineBarn = new Barn();
        this.tilknytningTilUtland = new TilknytningTilUtland();
    }

    public boolean erGyldig() {
        return this.veiledning.erGyldig() && this.oppsummering.erGyldig();
    }

    public void markerInnsendingsTidspunkt() {
        innsendingsTidspunkt = now();
    }

    public Barn getMineBarn() {
        return mineBarn;
    }
}
