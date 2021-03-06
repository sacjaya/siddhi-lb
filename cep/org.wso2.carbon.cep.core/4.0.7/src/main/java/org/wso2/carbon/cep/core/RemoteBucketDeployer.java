/*
* Copyright 2004,2005 The Apache Software Foundation.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.wso2.carbon.cep.core;


import org.wso2.carbon.automation.api.clients.cep.CEPAdminServiceClient;
import org.wso2.carbon.cep.core.internal.client.AuthenticationAdminServiceClient;
import org.wso2.carbon.cep.core.internal.util.ProductConstants;
import org.wso2.carbon.cep.stub.admin.internal.xsd.BucketDTO;



public class RemoteBucketDeployer {

    private String adminCookie;
    private String authenticationAdminURL;
    private String cepAdminServiceURL;
    private static final String ADMIN_SERVICE = "AuthenticationAdmin";

    public void deploy(String ip, BucketDTO bucket) throws Exception {

        String serviceURL = "https://" + ip + ":" + ProductConstants.HTTPS_PORT + "/services/";
        authenticationAdminURL = serviceURL + ADMIN_SERVICE;
        cepAdminServiceURL = serviceURL ;

        AuthenticationAdminServiceClient.init(authenticationAdminURL);
        AuthenticationAdminServiceClient.setSystemProperties(ProductConstants.CLIENT_TRUST_STORE_PATH, ProductConstants.KEY_STORE_TYPE, ProductConstants.KEY_STORE_PASSWORD);

        adminCookie = AuthenticationAdminServiceClient.login(ip, ProductConstants.USER_NAME, ProductConstants.PASSWORD);

        if (adminCookie == null) {
             throw new RuntimeException("could not login to the back-end server");
        }

        CEPAdminServiceClient cepAdminServiceClient =  new CEPAdminServiceClient(cepAdminServiceURL,adminCookie);
        cepAdminServiceClient.addBucket(bucket);


    }





}
