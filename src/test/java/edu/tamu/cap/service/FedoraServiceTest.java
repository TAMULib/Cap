package edu.tamu.cap.service;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.cap.CapApplication;
import edu.tamu.cap.model.IR;
import edu.tamu.cap.model.response.IRContext;
import edu.tamu.cap.model.response.Version;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { CapApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class FedoraServiceTest {

    private final static String ROOT_CONTEXT_URI = "http://localhost:9100/mock/fcrepo/rest";

    private final static String TEST_VERSION1_NAME = "TestVersion1";

    private final static String TEST_VERSION2_NAME = "TestVersion2";

    private final static String TEST_CONTEXT_URI = ROOT_CONTEXT_URI + "/path/to/container";

    @Autowired
    private FedoraService fedoraService;

    @Before
    public void setup() {
        fedoraService.setIr(new IR(IRType.FEDORA, "Mock Fedora", "http://localhost:9100/mock/fcrepo/rest"));
    }

    @Test
    public void createVersion() throws Exception {
        IRContext context = fedoraService.createVersion(TEST_CONTEXT_URI, TEST_VERSION1_NAME);
        assertVersions(context.getVersions());
    }

    @Test
    public void getVersions() throws Exception {
        List<Version> versions = fedoraService.getVersions(TEST_CONTEXT_URI);
        assertVersions(versions);
    }

    private void assertVersions(List<Version> versions) {
        assertEquals("Context had incorrect number of versions!", 2, versions.size());

        assertEquals("Version 1 had the incorrect name!", TEST_VERSION1_NAME, versions.get(0).getName());
        assertEquals("Version 1 had the incorrect time!", "2018-03-21T19:43:34.573Z^^http://www.w3.org/2001/XMLSchema#dateTime", versions.get(0).getTime());
        assertEquals("Version 1 had the incorrect subject!", "http://localhost:9100/mock/fcrepo/rest/path/to/container/fcr:versions", versions.get(0).getTriple().getSubject());
        assertEquals("Version 1 had the incorrect object!", "http://fedora.info/definitions/v4/repository#hasVersion", versions.get(0).getTriple().getPredicate());
        assertEquals("Version 1 had the incorrect predicate!", "http://localhost:9100/mock/fcrepo/rest/path/to/container/fcr:versions/TestVersion1", versions.get(0).getTriple().getObject());

        assertEquals("Version 2 had the incorrect name!", TEST_VERSION2_NAME, versions.get(1).getName());
        assertEquals("Version 2 had the incorrect time!", "2018-03-21T19:44:48.852Z^^http://www.w3.org/2001/XMLSchema#dateTime", versions.get(1).getTime());
        assertEquals("Version 2 had the incorrect subject!", "http://localhost:9100/mock/fcrepo/rest/path/to/container/fcr:versions", versions.get(1).getTriple().getSubject());
        assertEquals("Version 2 had the incorrect object!", "http://fedora.info/definitions/v4/repository#hasVersion", versions.get(1).getTriple().getPredicate());
        assertEquals("Version 2 had the incorrect predicate!", "http://localhost:9100/mock/fcrepo/rest/path/to/container/fcr:versions/TestVersion2", versions.get(1).getTriple().getObject());
    }

}
