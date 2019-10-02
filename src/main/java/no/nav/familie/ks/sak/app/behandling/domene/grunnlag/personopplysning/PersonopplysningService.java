package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning;

import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonopplysningService {

    private PersonopplysningGrunnlagRepository personopplysningGrunnlagRepository;
    private PersonRepository personRepository;

    @Autowired
    public PersonopplysningService(PersonopplysningGrunnlagRepository personopplysningGrunnlagRepository,
                                   PersonRepository informasjonRepository) {
        this.personopplysningGrunnlagRepository = personopplysningGrunnlagRepository;
        this.personRepository = informasjonRepository;
    }

    /**
     * Henter det aktive grunnlaget
     *
     * @param behandling
     * @return grunnlaget
     */
    public Optional<PersonopplysningGrunnlag> hentHvisEksisterer(Behandling behandling) {
        return personopplysningGrunnlagRepository.findByBehandlingAndAktiv(behandling.getId());
    }

    public void lagre(Behandling behandling, PersonopplysningGrunnlag personopplysningGrunnlag) {
        final var aktivtGrunnlag = hentHvisEksisterer(behandling);
        aktivtGrunnlag.ifPresent(gr -> {
            gr.setAktiv(false);
            personopplysningGrunnlagRepository.saveAndFlush(gr);
        });

        personopplysningGrunnlagRepository.save(personopplysningGrunnlag);
    }


    public void lagre(Behandling behandling) {
        final var aktivtGrunnlag = hentHvisEksisterer(behandling);
        aktivtGrunnlag.ifPresent(gr -> {
            gr.setAktiv(false);
            personopplysningGrunnlagRepository.saveAndFlush(gr);
        });
        final var registrertePersoner = aktivtGrunnlag.flatMap(PersonopplysningGrunnlag::getRegistrertePersoner).orElse(null);
        final var nyttGrunnlag = new PersonopplysningGrunnlag(behandling.getId(), registrertePersoner);
        personopplysningGrunnlagRepository.save(nyttGrunnlag);
    }
}
