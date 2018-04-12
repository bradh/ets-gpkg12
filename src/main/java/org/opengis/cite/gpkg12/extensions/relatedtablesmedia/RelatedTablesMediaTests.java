package org.opengis.cite.gpkg12.extensions.relatedtablesmedia;

import java.sql.PreparedStatement;
import static org.testng.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.opengis.cite.gpkg12.CommonFixture;
import org.opengis.cite.gpkg12.ErrorMessage;
import org.opengis.cite.gpkg12.ErrorMessageKeys;
import org.opengis.cite.gpkg12.util.DatabaseUtility;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Defines test methods that apply to the Media conformance class of the GeoPackage Related Tables extension.
 *
 * <p style="margin-bottom: 0.5em">
 * <strong>Sources</strong>
 * </p>
 * <ul>
 * <li><a href="TODO" target= "_blank">
 * OGC GeoPackage Related Tables Extension (DRAFT)</a> (OGC 18-000)</li>
 * </ul>
 *
 * @author Brad Hards
 */
public class RelatedTablesMediaTests extends CommonFixture {

    /**
     * Test case {@code /conf/media/udmt}
     *
     * Verify whether the Related Tables Extension Media Conformance Class is applicable (Req 12)
     *
     * @param testContext test context provided by calling test harness.
     *
     * @throws SQLException If an SQL query causes an error
     */
    @BeforeClass
    public void activeExtension(ITestContext testContext) throws SQLException {
        assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, "gpkg_extensions"), ErrorMessage.format(ErrorMessageKeys.CONFORMANCE_CLASS_NOT_USED, "Related Tables Extension Media Conformance Class"));
        assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, "gpkgext_relations"), ErrorMessage.format(ErrorMessageKeys.CONFORMANCE_CLASS_NOT_USED, "Related Tables Extension Media Conformance Class"));
        try (
                final Statement statement = this.databaseConnection.createStatement();
                final ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM gpkgext_relations WHERE relation_name = 'media'");) {
            resultSet.next();
            assertNotEquals(resultSet.getInt(1), 0, ErrorMessage.format(ErrorMessageKeys.CONFORMANCE_CLASS_NOT_USED, "Related Tables Extension Media Conformance Class"));
        }
    }


    /**
     * Test case {@code /conf/media/table_def}
     *
     * Verify that the media table has the right structure.
     *
     * @throws SQLException If an SQL query causes an error
     */
    @Test(description = "See OGC 18-000: Requirement 13")
    public void media_table_definition() throws SQLException {
        try (final PreparedStatement preparedStatement = this.databaseConnection.prepareStatement("SELECT related_table_name FROM gpkgext_relations WHERE relation_name = 'media'")) {
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    final String media_table_name = resultSet.getString("related_table_name");
                    check_media_table_definition(media_table_name);
                }
            }
        }
    }

    private void check_media_table_definition(String mediaTable) throws SQLException {
        try (
                final Statement statement = this.databaseConnection.createStatement();
                final ResultSet resultSet = statement.executeQuery("PRAGMA table_info('" + mediaTable + "')");) {
            int passFlag = 0;
            final int flagMask = 0b011;

            String pk = getPrimaryKeyColumn(mediaTable);
            assertNotNull(pk, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_ATTRIBUTES_NO_PRIMARY_KEY, mediaTable));

            while (resultSet.next()) {
                final String name = resultSet.getString("name");
                if ("data".equals(name)) {
                    assertEquals(resultSet.getString("type"), "BLOB", ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_MEDIA_COLUMN_INVALID, mediaTable, "data type"));
                    assertEquals(resultSet.getInt("notnull"), 1, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_MEDIA_COLUMN_INVALID, mediaTable, "data notnull"));
                    assertEquals(resultSet.getString("dflt_value"), null, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_MEDIA_COLUMN_INVALID, mediaTable, "base_id default value"));
                    assertEquals(resultSet.getInt("pk"), 0, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_MEDIA_COLUMN_INVALID, mediaTable, "base_id primary key"));
                    // TODO: unique key
                    passFlag |= (1);
                } else if ("content_type".equals(name)) {
                    assertEquals(resultSet.getString("type"), "TEXT", ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_MEDIA_COLUMN_INVALID, mediaTable, "content_type type"));
                    assertEquals(resultSet.getInt("notnull"), 1, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_MEDIA_COLUMN_INVALID, mediaTable, "content_type notnull"));
                    assertEquals(resultSet.getString("dflt_value"), null, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_MEDIA_COLUMN_INVALID, mediaTable, "base_id default value"));
                    assertEquals(resultSet.getInt("pk"), 0, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_MEDIA_COLUMN_INVALID, mediaTable, "base_id primary key"));
                    // TODO: unique key
                    passFlag |= (1 << 1);
                }
            }
            assertTrue((passFlag & flagMask) == flagMask, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_MEDIA_COLUMN_INVALID, mediaTable, "missing column(s)"));
        }
    }
}
