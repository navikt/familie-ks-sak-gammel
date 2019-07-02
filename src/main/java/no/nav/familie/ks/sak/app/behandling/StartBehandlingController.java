package no.nav.familie.ks.sak.app.behandling;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

@Component
@Path("behandling")
public class StartBehandlingController {

    public StartBehandlingController() {
    }

    @POST
    @Path("/start")
    @Consumes(APPLICATION_JSON)
    public Response startBehandling(String soeknad) {
        return Response.ok().build();
    }
}
