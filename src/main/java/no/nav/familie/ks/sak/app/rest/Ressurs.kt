package no.nav.familie.ks.sak.app.rest

import com.fasterxml.jackson.databind.JsonNode

data class Ressurs(val data: JsonNode?, val status: Status, val melding: String) {
    enum class Status { SUKSESS, FEILET, IKKE_HENTET }

    companion object {
        fun success(data: JsonNode) = Ressurs(
            data = data,
            status = Status.SUKSESS,
            melding = "Innhenting av data var vellykket"
        )

        fun failure(errorMessage: String? = null, error: Throwable? = null) = Ressurs(
            data = null,
            status = Status.FEILET,
            melding = errorMessage ?: "Kunne ikke hente data: ${error?.message}"
        )
    }
}
