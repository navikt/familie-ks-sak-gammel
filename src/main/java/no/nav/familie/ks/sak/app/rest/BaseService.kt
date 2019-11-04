package no.nav.familie.ks.sak.app.rest

import no.nav.security.token.support.client.core.ClientProperties
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService
import no.nav.security.token.support.client.spring.ClientConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.web.client.RestTemplate
import java.net.http.HttpRequest
import java.util.*

open class BaseService(clientConfigKey: String, restTemplateBuilder: RestTemplateBuilder,
                       clientConfigurationProperties: ClientConfigurationProperties,
                       private val oAuth2AccessTokenService: OAuth2AccessTokenService) {

    val restTemplate: RestTemplate = restTemplateBuilder
            .interceptors(bearerTokenInterceptor())
            .build()
    private val clientProperties: ClientProperties = Optional.ofNullable(
            clientConfigurationProperties.registration[clientConfigKey])
            .orElseThrow { RuntimeException("could not find oauth2 client config for key=$clientConfigKey") }

    private fun bearerTokenInterceptor(): (HttpRequest, byte[], ClientHttpRequestExecution) -> ClientHttpRequestInterceptor {
        return { (request: HttpRequest, body: byte[], execution: ClientHttpRequestExecution) ->
            val response = oAuth2AccessTokenService.getAccessToken(clientProperties)
            request.getHeaders().setBearerAuth(response.getAccessToken())
            execution.execute(request, body)
        }
    }
}
