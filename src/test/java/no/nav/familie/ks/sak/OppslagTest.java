package no.nav.familie.ks.sak;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.familie.ks.sak.app.grunnlag.Oppslag;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.status.PersonstatusType;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import static org.assertj.core.api.Assertions.assertThat;
public class OppslagTest {

    private static final ObjectMapper oppslagMapper = Oppslag.mapper;
    private static final String NORGE = "NOR";

    @Test
    public void personhistorikk_deserialiseres() throws IOException {
        File personhistorikkResponseBody = new File(getFile("personhistorikk.json"));
        PersonhistorikkInfo personhistorikkInfo = oppslagMapper.readValue(personhistorikkResponseBody, PersonhistorikkInfo.class);
        assertThat(personhistorikkInfo.getAdressehistorikk().get(0).getAdresse().getLand()).isEqualTo(NORGE);
        assertThat(personhistorikkInfo.getStatsborgerskaphistorikk().get(0).getTilhørendeLand().getKode()).isEqualTo(NORGE);
        assertThat(personhistorikkInfo.getPersonstatushistorikk().get(0).getPersonstatus()).isEqualByComparingTo(PersonstatusType.BOSA);
        assertThat(personhistorikkInfo.getAktørId()).isNotEmpty();
    }

    private String getFile(String filnavn) {
        return getClass().getClassLoader().getResource(filnavn).getFile();
    }
}
