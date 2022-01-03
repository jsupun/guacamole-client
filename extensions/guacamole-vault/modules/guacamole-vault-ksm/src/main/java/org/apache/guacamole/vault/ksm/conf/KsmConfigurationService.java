/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.guacamole.vault.ksm.conf;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.environment.Environment;
import org.apache.guacamole.properties.BooleanGuacamoleProperty;
import org.apache.guacamole.properties.IntegerGuacamoleProperty;
import org.apache.guacamole.properties.StringGuacamoleProperty;
import org.apache.guacamole.vault.conf.VaultConfigurationService;
import com.keepersecurity.secretsManager.core.LocalConfigStorage;
import com.keepersecurity.secretsManager.core.SecretsManagerOptions;

/**
 * Service for retrieving configuration information regarding the Keeper
 * Secrets Manager authentication extension.
 */
@Singleton
public class KsmConfigurationService extends VaultConfigurationService {

    /**
     * The Guacamole server environment.
     */
    @Inject
    private Environment environment;

    /**
     * The name of the file which contains the YAML mapping of certs, keys, and config for
     * Keeper Secrets Manager secret.
     */
    private static final String CONFIG_FILENAME = "keeper-secrets-manager-config.yml";


    /**
     * Client Id
     */
    private static final StringGuacamoleProperty CLIENT_ID = new StringGuacamoleProperty() {

        @Override
        public String getName() {
            return "clientId";
        }
    };

    /**
     * Private Key
     */
    private static final StringGuacamoleProperty PRIVATE_KEY = new StringGuacamoleProperty() {

        @Override
        public String getName() {
            return "privateKey";
        }
    };

    /**
     * App Key
     */
    private static final StringGuacamoleProperty APP_KEY = new StringGuacamoleProperty() {

        @Override
        public String getName() {
            return "appKey";
        }
    };

    /**
     * Hostname
     */
    private static final StringGuacamoleProperty HOSTNAME = new StringGuacamoleProperty() {

        @Override
        public String getName() {
            return "hostname";
        }
    };

    /**
     * Server Public Key Id
     */
    private static final IntegerGuacamoleProperty SERVER_PUBLIC_KEY_ID = new IntegerGuacamoleProperty() {

        @Override
        public String getName() {
            return "serverpublickeyid";
        }
    };

    /**
     * Allow Unverified Certificate
     */
    private static final BooleanGuacamoleProperty ALLOW_UNVERIFIED_CERT = new BooleanGuacamoleProperty() {

        @Override
        public String getName() {
            return "allowUnverifiedCertificate";
        }
    };

    /**
     * Creates a new KsmConfigurationService which reads the configuration
     * from "keeper-secrets-manager-config.yml". The configuration is a
     * YAML file which lists each keys, ids, and other values need for the
     * SDK to connect to the secret manager service.
     */
    public KsmConfigurationService() {
        super(CONFIG_FILENAME);
    }

    /**
     * Returns client id from the config
     *
     * @return
     *     Returns the client id
     *
     * @throws GuacamoleException
     *     If the client id is not specified within guacamole.properties.
     */
    public String getClientId() throws GuacamoleException {
        return environment.getRequiredProperty(CLIENT_ID);
    }

    /**
     * Returns private key from the config
     *
     * @return
     *     Returns the private key
     *
     * @throws GuacamoleException
     *     If the private key is not specified within guacamole.properties.
     */
    public String getPrivateKey() throws GuacamoleException {
        return environment.getRequiredProperty(PRIVATE_KEY);
    }

    /**
     * Returns app key from the config
     *
     * @return
     *     Returns the app key
     *
     * @throws GuacamoleException
     *     If the app key is not specified within guacamole.properties.
     */
    public String getAppKey() throws GuacamoleException {
        return environment.getRequiredProperty(APP_KEY);
    }

    /**
     * Returns hostname from the config
     *
     * @return
     *     Returns the hostname
     *
     * @throws GuacamoleException
     *     If the app key is not specified within guacamole.properties.
     */
    public String getHostname() throws GuacamoleException {
        return environment.getRequiredProperty(HOSTNAME);
    }

    /**
     * Returns the id of the public key.
     *
     * @return
     *     The id of the public key.
     *
     * @throws GuacamoleException
     *     If the value specified within guacamole.properties cannot be
     *     parsed.
     */
    public int getServerPublicKeyId() throws GuacamoleException {
        return environment.getProperty(SERVER_PUBLIC_KEY_ID, 10);
    }

    /**
     * Returns a flag indicating if we are allowing unverified certificates.
     *
     * @return
     *     Flag indicating if we are allowing unverified certificates.
     *
     * @throws GuacamoleException
     *     If the value specified within guacamole.properties cannot be
     *     parsed.
     */
    public boolean getAllowUnverifiedCertificate() throws GuacamoleException {
        return environment.getProperty(ALLOW_UNVERIFIED_CERT, false);
    }

    /**
     * Returns the credentials that should be used to authenticate with Azure
     * Key Vault when retrieving secrets. Azure's "ADAL" authentication will be
     * used, requiring a client ID and key. These values are specified with the
     * "azure-keyvault-client-id" and "azure-keyvault-client-key" properties
     * respectively.
     *
     * @return
     *     The credentials that should be used to authenticate with Azure Key
     *     Vault.
     *
     * @throws GuacamoleException
     *     If the client ID or key are not specified within
     *     guacamole.properties.
     */
    public SecretsManagerOptions getSecretsManagerOptions() throws GuacamoleException {

        LocalConfigStorage storage = new LocalConfigStorage();
        storage.saveString("clientId", getClientId());
        storage.saveString("privateKey", getPrivateKey());
        storage.saveString("appKey", getAppKey());
        storage.saveString("hostname", getHostname());
        storage.saveString("serverPublicKeyId", String.valueOf(getServerPublicKeyId()));

        return new SecretsManagerOptions(
                storage,
                null,
                getAllowUnverifiedCertificate());
    }
}
