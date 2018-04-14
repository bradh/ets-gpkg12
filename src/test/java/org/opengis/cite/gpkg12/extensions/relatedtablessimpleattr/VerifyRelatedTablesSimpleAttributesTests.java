package org.opengis.cite.gpkg12.extensions.relatedtablessimpleattr;

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

public class VerifyRelatedTablesSimpleAttributesTests {

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
        RelatedTablesSimpleAttributesTests tests = new RelatedTablesSimpleAttributesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Conformance class Related Tables Extension Simple Attributes Conformance Class is not in use.");
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
        RelatedTablesSimpleAttributesTests tests = new RelatedTablesSimpleAttributesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Conformance class Related Tables Extension Simple Attributes Conformance Class is not in use.");
        tests.activeExtension(testContext);
    }

    /**
     * Verifies that the Related Tables Extension test is not applicable if the
     * extension is present but there are no simple attributes entries.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void applicabilityIsNotApplicableNoEntries() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/related_media_empty.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesSimpleAttributesTests tests = new RelatedTablesSimpleAttributesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Conformance class Related Tables Extension Simple Attributes Conformance Class is not in use.");
        tests.activeExtension(testContext);
    }

    /**
     * Verifies that the Related Tables Extension test is applicable if there is a simple attributes entry.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void applicabilityIsApplicable() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesSimpleAttributesTests tests = new RelatedTablesSimpleAttributesTests();
        tests.initCommonFixture(testContext);
        tests.activeExtension(testContext);
    }
    
    
    /**
     * Verifies that the simple attributes table structure test passes if valid.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void attrIsValidStructure() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
        RelatedTablesSimpleAttributesTests tests = new RelatedTablesSimpleAttributesTests();
        tests.initCommonFixture(testContext);
        tests.simple_attr_table_definition();
    }
    
    /**
     * Verifies that the simple attributes table structure test fails if there is no primary key.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void attrIsInValidStructurePK() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related_nopk.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
         RelatedTablesSimpleAttributesTests tests = new RelatedTablesSimpleAttributesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("The features or attributes table info does not have a primary key. expected [true] but found [false]");
        tests.simple_attr_table_definition();
    }
    
    /**
     * Verifies that the simple attributes table structure test fails if there are no attributes.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void attrIsInValidStructureAttributeCount() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related_noattrs.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
         RelatedTablesSimpleAttributesTests tests = new RelatedTablesSimpleAttributesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Table info is missing at least one column. expected [true] but found [false]");
        tests.simple_attr_table_definition();
    }
    
    /**
     * Verifies that the simple attributes table structure test fails if there is an attribute that is nullable.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void attrIsInValidStructureAttributeNull() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related_bad_null.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
         RelatedTablesSimpleAttributesTests tests = new RelatedTablesSimpleAttributesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Column label of table info is supposed to have a notnull of 1 but found 0.");
        tests.simple_attr_table_definition();
    }
    
    /**
     * Verifies that the simple attributes table structure test fails if there is a BLOB column.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void attrIsInValidStructureAttributeBlob() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related_bad_type_blob.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
         RelatedTablesSimpleAttributesTests tests = new RelatedTablesSimpleAttributesTests();
        tests.initCommonFixture(testContext);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("The simple attributes table info has invalid data type BLOB.");
        tests.simple_attr_table_definition();
    }
    
    /**
     * Verifies that the simple attributes table structure test passes if there only valid types.
     *
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    @Test
    public void attrIsValidStructureAttributeTypes() throws IOException, SQLException, URISyntaxException {
        URL gpkgUrl = ClassLoader.getSystemResource("gpkg/simple_related_valid_types.gpkg");
        File dataFile = new File(gpkgUrl.toURI());
        dataFile.setWritable(false);
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName())).thenReturn(dataFile);
         RelatedTablesSimpleAttributesTests tests = new RelatedTablesSimpleAttributesTests();
        tests.initCommonFixture(testContext);
        tests.simple_attr_table_definition();
    }
}

