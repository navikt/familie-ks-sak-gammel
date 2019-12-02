package no.nav.familie.ks.sak.app.grunnlag;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.familie.ks.sak.app.integrasjon.medlemskap.MedlemskapsInfo;
import no.nav.familie.ks.sak.app.integrasjon.medlemskap.PeriodeStatusÅrsak;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.status.PersonstatusType;
import no.nav.familie.ks.sak.config.JacksonJsonConfig;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
public class IntegrasjonTjenesteTest {

    private static final ObjectMapper MAPPER = new JacksonJsonConfig().objectMapper();
    private static final String NORGE = "NOR";

    @Test
    public void personhistorikk_deserialiseres() throws IOException {
        File personhistorikkResponseBody = new File(getFile("personhistorikk.json"));
        PersonhistorikkInfo personhistorikkInfo = MAPPER.readValue(personhistorikkResponseBody, PersonhistorikkInfo.class);
        assertThat(personhistorikkInfo.getAdressehistorikk().get(0).getAdresse().getLand()).isEqualTo(NORGE);
        assertThat(personhistorikkInfo.getStatsborgerskaphistorikk().get(0).getTilhørendeLand().getKode()).isEqualTo(NORGE);
        assertThat(personhistorikkInfo.getPersonstatushistorikk().get(0).getPersonstatus()).isEqualByComparingTo(PersonstatusType.BOSA);
        assertThat(personhistorikkInfo.getPersonIdent().getIdent()).isNotEmpty();
    }

    @Test
    public void medlemskapsinfo_deserialiseres() throws IOException {
        File medlemskapsinfoResponseBody = new File(getFile("medlemskapsInfo.json"));
        MedlemskapsInfo medlemskapsInfo = MAPPER.readValue(medlemskapsinfoResponseBody, MedlemskapsInfo.class);

        assertThat(medlemskapsInfo.getGyldigePerioder().size()).isEqualTo(1);
        assertThat(medlemskapsInfo.getGyldigePerioder().get(0).getPeriodeStatusÅrsak()).isNull();
        assertThat(medlemskapsInfo.getAvvistePerioder().get(0).getPeriodeStatusÅrsak()).isEqualTo(PeriodeStatusÅrsak.Feilregistrert);
        assertThat(medlemskapsInfo.getUavklartePerioder().get(0).isGjelderMedlemskapIFolketrygden()).isFalse();
    }

    private String getFile(String filnavn) {
        return getClass().getClassLoader().getResource(filnavn).getFile();
    }
}
