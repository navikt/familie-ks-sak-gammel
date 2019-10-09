package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.personinformasjon

import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonAdresse
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.AdresseType
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Landkode
import no.nav.familie.ks.sak.app.behandling.domene.typer.DatoIntervallEntitet

data class RestAdresseinfo(
    val adresseType: AdresseType,
    val adresselinje1: String,
    val adresselinje2: String?,
    val adresselinje3: String?,
    val adresselinje4: String?,
    val postnummer: String?,
    val poststed: String?,
    val land: Landkode,
    val periode: DatoIntervallEntitet)

fun PersonAdresse.toRestAdresseInfo() = RestAdresseinfo(
    adresseType = this.adresseType,
    adresselinje1 = this.adresselinje1,
    adresselinje2 = this.adresselinje2,
    adresselinje3 = this.adresselinje3,
    adresselinje4 = this.adresselinje4,
    postnummer = this.postnummer,
    poststed = this.poststed,
    land = this.land,
    periode = this.periode
)
