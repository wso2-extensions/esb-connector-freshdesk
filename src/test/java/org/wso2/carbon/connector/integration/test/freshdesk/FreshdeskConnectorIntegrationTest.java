/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.connector.integration.test.freshdesk;

import org.apache.axiom.om.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FreshdeskConnectorIntegrationTest extends ConnectorIntegrationTestBase {

    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        init("freshdesk-connector-2.0.1-SNAPSHOT");
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        String authString = connectorProperties.getProperty("apiKey") + ":X";
        String authorizationHeader = "Basic " + Base64.encode(authString.getBytes());
        apiRequestHeadersMap.put("Authorization", authorizationHeader);
    }

    /**
     * Positive test case for createCompany method with mandatory parameters.
     */
    @Test(priority = 1, description = "FreshDesk {createCompany} integration test with mandatory parameters.")
    public void testCreateCompanyWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createCompany");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCompany_mandatory.json");
        String companyId = String.valueOf(esbRestResponse.getBody().getLong("id"));
        connectorProperties.put("companyId", companyId);
        String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/companies/" + companyId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
    }

    /**
     * Positive test case for createCompany method with optional parameters.
     */
    @Test(priority = 1, description = "FreshDesk {createCompany} integration test with mandatory parameters.")
    public void testCreateCompanyWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createCompany");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCompany_optional.json");
        String companyIdOptional = String.valueOf(esbRestResponse.getBody().getLong("id"));
        connectorProperties.put("companyIdOptional", companyIdOptional);

        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/companies/" + companyIdOptional;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
    }

    /**
     * Negative test case for createCompany method.
     */
    @Test(priority = 1, description = "FreshDesk {createCompany} integration test with mandatory parameters.")
    public void testCreateCompanyWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createCompany");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCompany_negative.json");
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/companies";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiUrl, "POST", apiRequestHeadersMap, "api_createCompany_negative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Positive test case for getCompany method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateCompanyWithMandatoryParameters"},
            description = "FreshDesk {getCompany} integration test with mandatory parameters.")
    public void testGetCompanyWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getCompany");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCompany_mandatory.json");
        final String apiUrl =
                connectorProperties.getProperty("apiUrl") + "/api/v2/companies/"
                        + connectorProperties.getProperty("companyId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Negative test case for getCompany method.
     */
    @Test(priority = 1, description = "FreshDesk {getCompany} integration test with negative parameters.")
    public void testGetCompanyWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getCompany");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCompany_negative.json");
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/companies/INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Test case for listCompanies method with mandatory parameter.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateCompanyWithMandatoryParameters"}, description = "freshdesk {listCompanies} integration test with mandatory parameters.")
    public void testListCompaniesWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listCompanies");
        final String apiUrl =
                connectorProperties.getProperty("apiUrl") + "/api/v2/companies/";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCompanies_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Test case for listCompanyFields method with mandatory parameter.
     */
    @Test(priority = 1, description = "freshdesk {listCompanyFields} integration test with mandatory parameters.")
    public void testListCompanyFieldsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listCompanyFields");
        final String apiUrl =
                connectorProperties.getProperty("apiUrl") + "/api/v2/company_fields";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCompanyFields_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Test case for updateCompany method with optional parameter.
     */
    @Test(enabled = true, dependsOnMethods = {"testGetCompanyWithMandatoryParameters"}, description = "freshdesk {updateCompany} integration test with optional parameters.")
    public void testUpdateCompanyWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateCompany");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v2/companies/" + connectorProperties.getProperty("companyId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateCompany_optional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
    }

    /**
     * Negative test case for updateCompany method.
     */
    @Test(priority = 1, description = "freshdesk {updateCompany} integration test with negative case.")
    public void testUpdateCompanyWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateCompany");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v2/companies/Negative";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateCompany_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Positive test case for deleteCompany method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testUpdateCompanyWithOptionalParameters"}, description = "FreshDesk {deleteCompany} integration test with mandatory parameters.")
    public void testDeleteCompanyWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:deleteCompany");
        final String apiUrl =
                connectorProperties.getProperty("apiUrl") + "/api/v2/companies/"
                        + connectorProperties.getProperty("companyId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteCompany_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Negative test case for deleteCompany method.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "freshdesk {deleteCompany} integration test with negative case.")
    public void testDeleteCompanyWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:deleteCompany");
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/companies/Invalid";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteCompany_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "DELETE", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Positive test case for getBusinessHour method with mandatory parameters.
     */
    @Test(priority = 1, description = "FreshDesk {getBusinessHour} integration test with mandatory parameters.")
    public void testGetBusinessHourWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getBusinessHour");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getBusinessHour_mandatory.json");
        final String apiUrl =
                connectorProperties.getProperty("apiUrl") + "/api/v2/business_hours/"
                        + connectorProperties.getProperty("businessHourId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Negative test case for getBusinessHour method.
     */
    @Test(priority = 1, description = "FreshDesk {getBusinessHour} integration test with negative parameters.")
    public void testGetBusinessHourWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getBusinessHour");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getBusinessHour_negative.json");
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/business_hours/INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Test case for listBusinessHours method with mandatory parameter.
     */
    @Test(priority = 1, description = "freshdesk {listBusinessHours} integration test with mandatory parameters.")
    public void testListBusinessHoursWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listBusinessHours");
        final String apiUrl =
                connectorProperties.getProperty("apiUrl") + "/api/v2/business_hours";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listBusinessHours_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for createComment method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateTopicWithOptionalParameters"}, description = "FreshDesk {createComment} integration test with mandatory parameters.")
    public void testCreateCommentWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createComment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComment_mandatory.json");
        String commentId = String.valueOf(esbRestResponse.getBody().getLong("id"));
        connectorProperties.put("commentId", commentId);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
    }

    /**
     * Negative test case for createComment method.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateTopicWithOptionalParameters"}, description = "FreshDesk {createComment} integration test with mandatory parameters.")
    public void testCreateCommentWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createComment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComment_negative.json");
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/discussions/topics/" + connectorProperties.getProperty("topicIdOptional") + "/comments";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiUrl, "POST", apiRequestHeadersMap, "api_createComment_negative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Positive test case for deleteComment method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateCommentWithMandatoryParameters"}, description = "FreshDesk {deleteComment} integration test with mandatory parameters.")
    public void testDeleteCommentWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:deleteComment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteComment_mandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
    }

    /**
     * Negative test case for deleteComment method.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "freshdesk {deleteComment} integration test with negative case.")
    public void testDeleteCommentWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:deleteComment");
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/discussions/comments/Invalid";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteComment_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "DELETE", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Positive test case for createContact method with mandatory parameters.
     */
    @Test(priority = 1, description = "FreshDesk {createContact} integration test with mandatory parameters.")
    public void testCreateContactWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_mandatory.json");
        String contactId = String.valueOf(esbRestResponse.getBody().getLong("id"));
        connectorProperties.put("contactId", contactId);

        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/contacts/" + contactId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("email"), apiRestResponse.getBody().getString("email"));
    }

    /**
     * Positive test case for createContact method with optional parameters.
     */
    @Test(priority = 1, description = "FreshDesk {createContact} integration test with mandatory parameters.")
    public void testCreateContactWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_optional.json");
        String contactIdOptional = String.valueOf(esbRestResponse.getBody().getLong("id"));
        connectorProperties.put("contactIdOptional", contactIdOptional);

        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/contacts/" + contactIdOptional;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("email"), apiRestResponse.getBody().getString("email"));
    }

    /**
     * Negative test case for createContact method.
     */
    @Test(priority = 1, description = "FreshDesk {createContact} integration test with mandatory parameters.")
    public void testCreateContactWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_negative.json");
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/contacts";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiUrl, "POST", apiRequestHeadersMap, "api_createContact_negative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Positive test case for getContact method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateContactWithMandatoryParameters"},
            description = "FreshDesk {getContact} integration test with mandatory parameters.")
    public void testGetContactWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContact_mandatory.json");
        final String apiUrl =
                connectorProperties.getProperty("apiUrl") + "/api/v2/contacts/"
                        + connectorProperties.getProperty("contactId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Negative test case for getContact method.
     */
    @Test(priority = 1, description = "FreshDesk {getContact} integration test with negative parameters.")
    public void testGetContactWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContact_negative.json");
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/contacts/INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Test case for listContacts method with mandatory parameter.
     */
    @Test(priority = 1, description = "freshdesk {listContacts} integration test with mandatory parameters.")
    public void testListContactsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listContacts");
        final String apiUrl =
                connectorProperties.getProperty("apiUrl") + "/api/v2/contacts";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Test case for listContacts method with optional parameter.
     */
    @Test(priority = 1, description = "freshdesk {listContacts} integration test with mandatory parameters.")
    public void testListContactsWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listContacts");
        final String apiUrl =
                connectorProperties.getProperty("apiUrl") + "/api/v2/contacts?company_id=19000007803";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_optional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for createNote method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateTicketWithMandatoryParameters"}, description = "FreshDesk {createNote} integration test with mandatory parameters.")
    public void testCreateNoteWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createNote");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createNote_mandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
    }

    /**
     * Positive test case for createNote method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateTicketWithMandatoryParameters"}, description = "FreshDesk {createNote} integration test with mandatory parameters.")
    public void testCreateNoteWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createNote");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createNote_optional.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
    }

    /**
     * Negative test case for createNote method.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateTicketWithMandatoryParameters"}, description = "FreshDesk {createNote} integration test with mandatory parameters.")
    public void testCreateNoteWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createNote");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createNote_negative.json");
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/tickets/7/notes";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiUrl, "POST", apiRequestHeadersMap, "api_createNote_negative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Positive test case for createReply method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateTicketWithMandatoryParameters"}, description = "FreshDesk {createReply} integration test with mandatory parameters.")
    public void testCreateReplyWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createReply");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createReply_mandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
    }

    /**
     * Positive test case for createReply method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateTicketWithMandatoryParameters"}, description = "FreshDesk {createReply} integration test with mandatory parameters.")
    public void testCreateReplyWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createReply");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createReply_optional.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
    }

    /**
     * Negative test case for createReply method.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateTicketWithMandatoryParameters"}, description = "FreshDesk {createReply} integration test with mandatory parameters.")
    public void testCreateReplyWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createReply");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createReply_negative.json");
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/tickets/7/reply";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiUrl, "POST", apiRequestHeadersMap, "api_createReply_negative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Positive test case for createForum method with mandatory parameters.
     */
    @Test(priority = 1, description = "FreshDesk {createForum} integration test with mandatory parameters.")
    public void testCreateForumWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createForum");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createForum_mandatory.json");
        String forumId = String.valueOf(esbRestResponse.getBody().getLong("id"));
        connectorProperties.put("forumId", forumId);
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/discussions/forums/" + connectorProperties.getProperty("forumId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
    }

    /**
     * Positive test case for createForum method with optional parameters.
     */
    @Test(priority = 1, description = "FreshDesk {createForum} integration test with mandatory parameters.")
    public void testCreateForumWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createForum");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createForum_optional.json");
        String forumIdOptional = String.valueOf(esbRestResponse.getBody().getLong("id"));
        connectorProperties.put("forumIdOptional", forumIdOptional);
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/discussions/forums/" + connectorProperties.getProperty("forumIdOptional");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
    }

    /**
     * Negative test case for createForum method.
     */
    @Test(priority = 1, description = "FreshDesk {createForum} integration test with mandatory parameters.")
    public void testCreateForumWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createForum");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createForum_negative.json");
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/discussions/categories/" + connectorProperties.getProperty("categoryId") + "/forums";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiUrl, "POST", apiRequestHeadersMap, "api_createForum_negative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Positive test case for getForum method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateForumWithMandatoryParameters"},
            description = "FreshDesk {getForum} integration test with mandatory parameters.")
    public void testGetForumWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getForum");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getForum_mandatory.json");
        final String apiUrl =
                connectorProperties.getProperty("apiUrl") + "/api/v2/discussions/forums/"
                        + connectorProperties.getProperty("forumId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Negative test case for getForum method.
     */
    @Test(priority = 1, description = "FreshDesk {getForum} integration test with negative parameters.")
    public void testGetForumWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getForum");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getForum_negative.json");
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/discussions/forums/INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Test case for updateForum method with optional parameter.
     */
    @Test(enabled = true, dependsOnMethods = {"testCreateForumWithOptionalParameters"}, description = "freshdesk {updateForum} integration test with optional parameters.")
    public void testUpdateForumWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateForum");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v2/discussions/forums/" + connectorProperties.getProperty("forumIdOptional");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateForum_optional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
    }

    /**
     * Negative test case for updateForum method.
     */
    @Test(priority = 1, description = "freshdesk {updateForum} integration test with negative case.")
    public void testUpdateForumWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateForum");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v2/discussions/forums/Negative";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateForum_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Positive test case for getProduct method with mandatory parameters.
     */
    @Test(priority = 1, description = "FreshDesk {getProduct} integration test with mandatory parameters.")
    public void testGetProductWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getProduct");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProduct_mandatory.json");
        final String apiUrl =
                connectorProperties.getProperty("apiUrl") + "/api/v2/products/"
                        + connectorProperties.getProperty("productId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Negative test case for getProduct method.
     */
    @Test(priority = 1, description = "FreshDesk {getProduct} integration test with negative parameters.")
    public void testGetProductWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getProduct");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProduct_negative.json");
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/products/Negative";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        System.out.print("Response ESB" + esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Test case for listProducts method with mandatory parameter.
     */
    @Test(priority = 1, description = "freshdesk {listProducts} integration test with mandatory parameters.")
    public void testListProductsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listProducts");
        final String apiUrl =
                connectorProperties.getProperty("apiUrl") + "/api/v2/products";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProducts_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Test case for listSLAPolicies method with mandatory parameter.
     */
    @Test(priority = 1, description = "freshdesk {listSLAPolicies} integration test with mandatory parameters.")
    public void testListSLAPoliciesWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listSLAPolicies");
        final String apiUrl =
                connectorProperties.getProperty("apiUrl") + "/api/v2/sla_policies";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSLAPolicies_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Test case for updateSLAPolicy method with mandatory parameter.
     */
    @Test(enabled = true, description = "freshdesk {updateSLAPolicy} integration test with mandatory parameters.")
    public void testupdateSLAPolicyWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateSLAPolicy");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateSLAPolicy_mandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Negative test case for updateSLAPolicy method.
     */
    @Test(priority = 1, description = "freshdesk {updateSLAPolicy} integration test with negative case.")
    public void testupdateSLAPolicyWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateSLAPolicy");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateSLAPolicy_negative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for createTicket method with mandatory parameters.
     */
    @Test(priority = 1, description = "FreshDesk {createTicket} integration test with mandatory parameters.")
    public void testCreateTicketWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createTicket");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTicket_mandatory.json");
        String ticketId = String.valueOf(esbRestResponse.getBody().getLong("id"));
        connectorProperties.put("ticketId", ticketId);

        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/tickets/" + ticketId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for createTicket method with optional parameters.
     */
    @Test(priority = 1, description = "FreshDesk {createTicket} integration test with optional parameters.")
    public void testCreateTicketWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createTicket");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTicket_optional.json");
        String ticketIdOptional = String.valueOf(esbRestResponse.getBody().getLong("id"));
        connectorProperties.put("ticketIdOptional", ticketIdOptional);

        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/tickets/" + ticketIdOptional;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for createTicket method.
     */
    @Test(priority = 1, description = "FreshDesk {createTicket} integration test with mandatory parameters.")
    public void testCreateTicketWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createTicket");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTicket_negative.json");
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/tickets";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiUrl, "POST", apiRequestHeadersMap, "api_createTicket_negative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Positive test case for getTicket method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateTicketWithMandatoryParameters"},
            description = "FreshDesk {getTicket} integration test with mandatory parameters.")
    public void testGetTicketWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getTicket");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTicket_mandatory.json");
        final String apiUrl =
                connectorProperties.getProperty("apiUrl") + "/api/v2/tickets/"
                        + connectorProperties.getProperty("ticketId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Negative test case for getTicket method.
     */
    @Test(priority = 1, description = "FreshDesk {getTicket} integration test with negative parameters.")
    public void testGetTicketWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getTicket");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTicket_negative.json");
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/tickets/INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Test case for listTicketFields method with mandatory parameter.
     */
    @Test(priority = 1, description = "freshdesk {listTicketFields} integration test with mandatory parameters.")
    public void testListTicketFieldsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listTicketFields");
        final String apiUrl =
                connectorProperties.getProperty("apiUrl") + "/api/v2/ticket_fields";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTicketFields_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for listTickets method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateTicketWithMandatoryParameters"}, description = "FreshDesk {listTickets} integration test with mandatory parameters.")
    public void testListTicketsWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listTickets");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTickets_mandatory.json");
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/tickets";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for listTickets method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateTicketWithMandatoryParameters"}, description = "FreshDesk {listTickets} integration test with optional parameters.")
    public void testListTicketsWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listTickets");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTickets_optional.json");
        final String apiUrl =
                connectorProperties.getProperty("apiUrl") + "/api/v2/tickets?filter="
                        + connectorProperties.getProperty("predefinedFilter") + "&order_type="
                        + connectorProperties.getProperty("orderType");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for deleteTicket method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateTicketWithOptionalParameters"}, description = "FreshDesk {deleteTicket} integration test with mandatory parameters.")
    public void testDeleteTicketWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:deleteTicket");
        final String apiUrl =
                connectorProperties.getProperty("apiUrl") + "/api/v2/tickets/"
                        + connectorProperties.getProperty("ticketIdOptional");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteTicket_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().getString("deleted"), "true");
    }

    /**
     * Negative test case for deleteTicket method.
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "freshdesk {deleteTicket} integration test with negative case.")
    public void testDeleteTicketWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:deleteTicket");
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/tickets/Invalid";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteTicket_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "DELETE", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Test case for updateTicket method with optional parameter.
     */
    @Test(enabled = true, dependsOnMethods = {"testCreateTicketWithMandatoryParameters"}, description = "freshdesk {updateForum} integration test with optional parameters.")
    public void testUpdateTicketWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateTicket");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v2/tickets/" + connectorProperties.getProperty("ticketId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTicket_optional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("description"), apiRestResponse.getBody().getString("description"));
    }

    /**
     * Negative test case for updateTicket method.
     */
    @Test(priority = 1, description = "freshdesk {updateTicket} integration test with negative case.")
    public void testUpdateTicketWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateTicket");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v2/tickets/Negative";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTicket_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Positive test case for createTopic method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateForumWithMandatoryParameters"}, description = "FreshDesk {createTopic} integration test with mandatory parameters.")
    public void testCreateTopicWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createTopic");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTopic_mandatory.json");
        String topicId = String.valueOf(esbRestResponse.getBody().getLong("id"));
        connectorProperties.put("topicId", topicId);

        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/discussions/topics/" + topicId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for createTopic method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateForumWithMandatoryParameters"}, description = "FreshDesk {createTopic} integration test with mandatory parameters.")
    public void testCreateTopicWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createTopic");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTopic_optional.json");
        String topicIdOptional = String.valueOf(esbRestResponse.getBody().getLong("id"));
        connectorProperties.put("topicIdOptional", topicIdOptional);
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/discussions/topics/" + topicIdOptional;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Negative test case for createTopic method.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateForumWithMandatoryParameters"}, description = "FreshDesk {createTopic} integration test with mandatory parameters.")
    public void testCreateTopicWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createTopic");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTopic_negative.json");
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/discussions/forums/19000062773/topics";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiUrl, "POST", apiRequestHeadersMap, "api_createTopic_negative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Positive test case for getTopic method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateTopicWithMandatoryParameters"},
            description = "FreshDesk {getTopic} integration test with mandatory parameters.")
    public void testGetTopicWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getTopic");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTopic_mandatory.json");
        final String apiUrl =
                connectorProperties.getProperty("apiUrl") + "/api/v2/discussions/topics/"
                        + connectorProperties.getProperty("topicId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Negative test case for getTopic method.
     */
    @Test(priority = 1, description = "FreshDesk {getTopic} integration test with negative parameters.")
    public void testGetTopicWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getTopic");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTopic_negative.json");
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/discussions/topics/INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Positive test case for deleteTopic method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testUpdateTopicWithOptionalParameters"}, description = "FreshDesk {deleteCompany} integration test with mandatory parameters.")
    public void testDeleteTopicWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:deleteTopic");
        final String apiUrl =
                connectorProperties.getProperty("apiUrl") + "/api/v2/discussions/topics/"
                        + connectorProperties.getProperty("topicId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteTopic_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Negative test case for deleteTopic method.
     */
    @Test(priority = 1, description = "freshdesk {deleteTopic} integration test with negative case.")
    public void testDeleteTopicWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:deleteTopic");
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2/discussions/topics/Invalid";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteTopic_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "DELETE", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Test case for updateTopic method with optional parameter.
     */
    @Test(enabled = true, dependsOnMethods = {"testCreateTopicWithMandatoryParameters"}, description = "freshdesk {updateTopic} integration test with optional parameters.")
    public void testUpdateTopicWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateTopic");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v2/discussions/topics/"
                + connectorProperties.getProperty("topicId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTopic_optional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Negative test case for updateTopic method.
     */
    @Test(priority = 1, description = "freshdesk {updateTopic} integration test with negative case.")
    public void testUpdateTopicWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateTopic");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v2/discussions/topics/Negative";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTopic_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
}
