package org.opengis.cite.gpkg12.extensions.relatedtablesmedia;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.opengis.cite.gpkg12.SuiteAttribute;
import org.testng.ISuite;
import org.testng.ITestContext;

public class VerifyRelatedTablesMediaTests {

    private static ITestContext testContext;
    private static ISuite suite;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void initTestFixture() {
        testContext = mock(ITestContext.class);
        suite = mock(ISuite.class);
        when(testContext.getSuite()).thenReturn(suite);
    }

    /**
     * Verifies that the Related Tables Extension test is not applicable if the
     * gpkg_extension table is not present.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void applicabilityIsNotApplicableNoExtensions() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/gdal_sample_v1.2_no_extensions.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesMediaTests tests = new RelatedTablesMediaTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Conformance class Related Tables Extension Media Conformance Class is not in use.");
        tests.activeExtension(testContext);
    }

    /**
     * Verifies that the Related Tables Extension test is not applicable if the
     * required extension is not present.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void applicabilityIsNotApplicable() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/gdal_sample_v1.2_spatial_index_extension.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesMediaTests tests = new RelatedTablesMediaTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Conformance class Related Tables Extension Media Conformance Class is not in use.");
        tests.activeExtension(testContext);
    }

    /**
     * Verifies that the Related Tables Extension test is not applicable if the
     * extension is present but there are no media entries.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void applicabilityIsNotApplicableNoEntries() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesMediaTests tests = new RelatedTablesMediaTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Conformance class Related Tables Extension Media Conformance Class is not in use.");
        tests.activeExtension(testContext);
    }

    /**
     * Verifies that the Related Tables Extension test is applicable if there is a media entry.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void applicabilityIsApplicable() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/related_media_empty.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesMediaTests tests = new RelatedTablesMediaTests();
        tests.initCommonFixture(testContext);
        tests.activeExtension(testContext);
    }
    
    
    /**
     * Verifies that the media table structure test passes if valid.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void mediaIsValidStructure() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/related_media_empty.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesMediaTests tests = new RelatedTablesMediaTests();
        tests.initCommonFixture(testContext);
        tests.media_table_definition();
    }
}
