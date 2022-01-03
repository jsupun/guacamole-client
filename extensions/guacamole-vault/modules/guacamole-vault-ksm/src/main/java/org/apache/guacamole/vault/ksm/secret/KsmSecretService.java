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

package org.apache.guacamole.vault.ksm.secret;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.concurrent.CompletableFuture;

import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.vault.ksm.conf.KsmConfigurationService;
import org.apache.guacamole.vault.ksm.notation.KsmNotation;
import org.apache.guacamole.vault.ksm.notation.KsmNotationItem;
import org.apache.guacamole.vault.secret.CachedVaultSecretService;

/**
 * Service which retrieves secrets from Azure Key Vault.
 */
@Singleton
public class KsmSecretService extends CachedVaultSecretService {

    // Time to live for the secret to be cached. Cache for 5 second.
    private static int ttl = 5000;

    /**
     * Service for retrieving configuration information.
     */
    @Inject
    private KsmConfigurationService confService;

    @Override
    protected CachedSecret refreshCachedSecret(String name)
            throws GuacamoleException {

        CompletableFuture<String> retrievedValue = new CompletableFuture<>();

        (new Thread() {

            @Override
            public void run() {
                try {
                    // Parse the notation into components and check if it's valid
                    KsmNotationItem notationItem = KsmNotation.parse(name);

                    // Get the value from the vault.
                    String value = notationItem.getValue(confService.getSecretsManagerOptions());

                    retrievedValue.complete(value);

                } catch (Exception e) {
                    retrievedValue.completeExceptionally(e);
                }
            }

        }).start();

        // Cache retrieved value
        return new CachedSecret(retrievedValue, KsmSecretService.ttl);
    }

    @Override
    public String canonicalize(String name) {
        return name;
    }
}