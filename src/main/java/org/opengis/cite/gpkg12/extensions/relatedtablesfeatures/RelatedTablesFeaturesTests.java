package org.opengis.cite.gpkg12.extensions.relatedtablesfeatures;

import java.io.IOException;
import java.sql.PreparedStatement;
import static org.testng.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.opengis.cite.gpkg12.CommonFixture;
import org.opengis.cite.gpkg12.ErrorMessage;
import org.opengis.cite.gpkg12.ErrorMessageKeys;
import org.opengis.cite.gpkg12.features.FeaturesTests;
import org.opengis.cite.gpkg12.util.DatabaseUtility;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Defines test methods that apply to the Related Features conformance class of
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
public class RelatedTablesFeaturesTests extends CommonFixture {

    /**
     * Test case {@code /conf/relatedfeat/udat}
     *
     * Verify whether the Related Tables Extension Related Features Conformance
     * Class is applicable (Req 16)
     *
     * @param testContext test context provided by calling test harness.
     *
     * @throws SQLException If an SQL query causes an error
     */
    @BeforeClass
    public void activeExtension(ITestContext testContext) throws SQLException {
        assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, "gpkg_extensions"), ErrorMessage.format(ErrorMessageKeys.CONFORMANCE_CLASS_NOT_USED, "Related Tables Extension Related Features Conformance Class"));
        assertTrue(DatabaseUtility.doesTableOrViewExist(this.databaseConnection, "gpkgext_relations"), ErrorMessage.format(ErrorMessageKeys.CONFORMANCE_CLASS_NOT_USED, "Related Tables Extension Related Features Conformance Class"));
        try (
                final Statement statement = this.databaseConnection.createStatement();
                final ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM gpkgext_relations WHERE relation_name = 'features'");) {
            resultSet.next();
            assertNotEquals(resultSet.getInt(1), 0, ErrorMessage.format(ErrorMessageKeys.CONFORMANCE_CLASS_NOT_USED, "Related Tables Extension Related Features Conformance Class"));
        }
    }

    /**
     * Test case {@code /conf/relatedfeat/table_def}
     *
     * Verify that the specified table is a proper features table.
     *
     * @throws SQLException If an SQL query causes an error
     * @throws IOException if the database can't be found
     */
    @Test(description = "See OGC 18-000: Requirement 17")
    public void related_features_table_definition() throws SQLException, IOException {
        try (final PreparedStatement preparedStatement = this.databaseConnection.prepareStatement("SELECT related_table_name FROM gpkgext_relations WHERE relation_name = 'features'")) {
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                ArrayList<String> featureTables = new ArrayList<>();
                while (resultSet.next()) {
                    final String features_table_name = resultSet.getString("related_table_name");
                    featureTables.add(features_table_name);
                }
                FeaturesTests featuresTests = new FeaturesTests();
                featuresTests.initCommonFixture(this.testContext);
                featuresTests.setFeatureTableNames(featureTables);
                featuresTests.featureTableIntegerPrimaryKey();
                featuresTests.featureTableGeometryColumnType();
                featuresTests.featureTableOneGeometryColumn();
                
                final Statement statement1 = this.databaseConnection.createStatement();
                for (String tableName : featureTables) {
                    final ResultSet resultSet1 = statement1.executeQuery(String.format("SELECT column_name FROM gpkg_geometry_columns WHERE table_name = '%s'", tableName));
                    while (resultSet1.next()) {
                        final String column_name = resultSet1.getString("column_name");
                        featuresTests.checkGeometryValues(tableName, column_name);
                    }
                }
                
            }
        }
    }
}
