package no.nav.familie.ks.sak.app.rest.behandling.resultat

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.InputType
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.UtfallType
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.VilkårType

data class RestVilkårsResultat(
    val vilkårType: InputType,
    val utfall: UtfallType)
