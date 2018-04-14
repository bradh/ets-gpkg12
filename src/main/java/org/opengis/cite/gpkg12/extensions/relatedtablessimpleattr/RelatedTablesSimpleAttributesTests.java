package org.opengis.cite.gpkg12.extensions.relatedtablessimpleattr;

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
 * Defines test methods that apply to the Simple Attributes conformance class of
 * the GeoPackage Related Tables extension.
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
public class RelatedTablesSimpleAttributesTests extends CommonFixture {

    /**
     * Test case {@code /conf/simpleattr/udat}
     *
     * Verify whether the Related Tables Extension Simple Attributes Conformance
     * Class is applicable (Req 14)
     *
     * @param testContext test context provided by calling test harness.
     *
     * @throws SQLException If an SQL query causes an error
     */
    @BeforeClass
    public void activeExtension(ITestContext testContext) throws SQLException {
        assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, "gpkg_extensions"), ErrorMessage.format(ErrorMessageKeys.CONFORMANCE_CLASS_NOT_USED, "Related Tables Extension Simple Attributes Conformance Class"));
        assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, "gpkgext_relations"), ErrorMessage.format(ErrorMessageKeys.CONFORMANCE_CLASS_NOT_USED, "Related Tables Extension Simple Attributes Conformance Class"));
        try (
                final Statement statement = this.databaseConnection.createStatement();
                final ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM gpkgext_relations WHERE relation_name = 'simple_attributes'");) {
            resultSet.next();
            assertNotEquals(resultSet.getInt(1), 0, ErrorMessage.format(ErrorMessageKeys.CONFORMANCE_CLASS_NOT_USED, "Related Tables Extension Simple Attributes Conformance Class"));
        }
    }

    /**
     * Test case {@code /conf/simpleattr/table_def}
     *
     * Verify that the simple attributes table has the right structure.
     *
     * @throws SQLException If an SQL query causes an error
     */
    @Test(description = "See OGC 18-000: Requirement 15")
    public void simple_attr_table_definition() throws SQLException {
        try (final PreparedStatement preparedStatement = this.databaseConnection.prepareStatement("SELECT related_table_name FROM gpkgext_relations WHERE relation_name = 'simple_attributes'")) {
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    final String simpleattr_table_name = resultSet.getString("related_table_name");
                    check_simple_attr_table_definition(simpleattr_table_name);
                }
            }
        }
    }

    private void check_simple_attr_table_definition(String attrTable) throws SQLException {
        try (
                final Statement statement = this.databaseConnection.createStatement();
                final ResultSet resultSet = statement.executeQuery("PRAGMA table_info('" + attrTable + "')");) {
            boolean foundValidColumn = false;

            String pk = getPrimaryKeyColumn(attrTable);
            assertNotNull(pk, ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_ATTRIBUTES_NO_PRIMARY_KEY, attrTable));

            while (resultSet.next()) {
                final String name = resultSet.getString("name");
                if (name.equals(pk)) {
                    continue;
                }
                checkOneColumnDefinition(resultSet, attrTable);
                foundValidColumn = true;
            }
            assertEquals(foundValidColumn, true, ErrorMessage.format(ErrorMessageKeys.MISSING_COLUMN, attrTable, "missing column(s)"));
        }
    }

    private void checkOneColumnDefinition(ResultSet resultSet, String attrTable) throws SQLException {
        // Column {0} of table {1} is supposed to have a {2} of {3} but found {4}.
        assertEquals(resultSet.getInt("notnull"), 1, ErrorMessage.format(ErrorMessageKeys.INVALID_COLUMN_DEFINITION, resultSet.getString("name"), attrTable, "notnull", 1, resultSet.getInt("notnull")));
        String affinity = getAffinity(resultSet.getString("type"));
        assertNotEquals(affinity, "BLOB", ErrorMessage.format(ErrorMessageKeys.RELATED_TABLES_SIMPLE_ATTR_COLUMN_INVALID, attrTable, resultSet.getString("type")));
    }

    private String getAffinity(String declaredType) {
        declaredType = declaredType.toUpperCase();
        if (declaredType == null) {
            return "BLOB";
        } else if (declaredType.contains("INT")) {
            return "INTEGER";
        } else if (declaredType.contains("CHAR") || declaredType.contains("CLOB") || declaredType.contains("TEXT")) {
            return "TEXT";
        } else if (declaredType.contains("BLOB")) {
            return "BLOB";
        } else if (declaredType.contains("REAL") || declaredType.contains("FLOA") || declaredType.contains("DOUB")) {
            return "REAL";
        } else {
            return "NUMERIC";
        }
    }
}
