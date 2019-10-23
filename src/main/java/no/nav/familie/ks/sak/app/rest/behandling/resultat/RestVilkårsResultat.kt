package no.nav.familie.ks.sak.app.rest.behandling.resultat

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.VilkårEllerAvvikType
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.UtfallType

data class RestVilkårsResultat(
        val vilkårType: VilkårEllerAvvikType,
        val utfall: UtfallType)
