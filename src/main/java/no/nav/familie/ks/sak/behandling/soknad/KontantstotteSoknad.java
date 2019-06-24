package no.nav.familie.ks.sak.behandling.soknad;

import java.time.Instant;

public class KontantstotteSoknad {

    public Veiledning veiledning;
    public Instant innsendingsTidspunkt;
    public Person person;
    public SokerKrav kravTilSoker;
    public Familieforhold familieforhold;
    public Barnehageplass barnehageplass;
    public ArbeidIUtlandet arbeidIUtlandet;
    public UtenlandskKontantstotte utenlandskKontantstotte;
    public Barn mineBarn;
    public TilknytningTilUtland tilknytningTilUtland;
    public UtenlandskeYtelser utenlandskeYtelser;
    public Oppsummering oppsummering;
    public String sprak;

    public KontantstotteSoknad() {
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
}
