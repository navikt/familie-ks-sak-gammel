package no.nav.familie.ks.sak.app.rest.behandling

import no.nav.familie.ks.sak.app.rest.behandling.grunnlag.personinformasjon.RestPersoner
import no.nav.familie.ks.sak.app.rest.behandling.grunnlag.søknad.RestSøknad
import no.nav.familie.ks.sak.app.rest.behandling.resultat.RestBehandlingsresultat

data class RestBehandling(
    val behandlingId: Long,
    val søknadGrunnlag: RestSøknad,
    val behandlingsresultat: RestBehandlingsresultat,
    val personopplysninger: RestPersoner?)
