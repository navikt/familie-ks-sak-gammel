package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning;

import org.springframework.data.jpa.repository.JpaRepository;

interface PersonopplysningInformasjonRepository extends JpaRepository<PersonopplysningerInformasjon, Long> {
}
