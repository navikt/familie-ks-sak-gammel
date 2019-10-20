package no.nav.familie.ks.sak.app.rest.tilgangskontroll;

import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.spring.SpringTokenValidationContextHolder;


public class OIDCUtil {

    public static String getSubjectFromAzureOIDCToken(TokenValidationContextHolder contextHolder, String issuerName) {
        return contextHolder.getTokenValidationContext().getClaims(issuerName).getSubject();
    }
}
