package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning;

import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonopplysningService {

    private PersonopplysningRepository personopplysningRepository;
    private PersonopplysningInformasjonRepository informasjonRepository;

    @Autowired
    public PersonopplysningService(PersonopplysningRepository personopplysningRepository,
                                   PersonopplysningInformasjonRepository informasjonRepository) {
        this.personopplysningRepository = personopplysningRepository;
        this.informasjonRepository = informasjonRepository;
    }

    /**
     * Henter det aktive grunnlaget
     *
     * @param behandling
     * @return grunnlaget
     */
    public Optional<PersonopplysningGrunnlag> hentHvisEksisterer(Behandling behandling) {
        return personopplysningRepository.findByBehandlingAndAktiv(behandling.getId());
    }

    public void lagre(Behandling behandling, PersonopplysningerInformasjon informasjon) {
        final var aktivtGrunnlag = hentHvisEksisterer(behandling);
        aktivtGrunnlag.ifPresent(gr -> {
            gr.setAktiv(false);
            personopplysningRepository.saveAndFlush(gr);
        });
        final var oppgittAnnenPart = aktivtGrunnlag.flatMap(PersonopplysningGrunnlag::getOppgittAnnenPart).orElse(null);
        final var nyttGrunnlag = new PersonopplysningGrunnlag(behandling, oppgittAnnenPart, informasjon);
        informasjonRepository.save(informasjon);
        personopplysningRepository.saveAndFlush(nyttGrunnlag);
    }


    public void lagre(Behandling behandling, AktørId annenPart) {
        final var aktivtGrunnlag = hentHvisEksisterer(behandling);
        aktivtGrunnlag.ifPresent(gr -> {
            gr.setAktiv(false);
            personopplysningRepository.saveAndFlush(gr);
        });
        final var informasjon = aktivtGrunnlag.flatMap(PersonopplysningGrunnlag::getRegisterVersjon).orElse(null);
        final var nyttGrunnlag = new PersonopplysningGrunnlag(behandling, annenPart, informasjon);
        informasjonRepository.save(informasjon);
        personopplysningRepository.saveAndFlush(nyttGrunnlag);
    }
}
