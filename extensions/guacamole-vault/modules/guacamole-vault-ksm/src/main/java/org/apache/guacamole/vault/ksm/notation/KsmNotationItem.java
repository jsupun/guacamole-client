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

package org.apache.guacamole.vault.ksm.notation;

import static com.keepersecurity.secretsManager.core.SecretsManager.downloadFile;
import com.keepersecurity.secretsManager.core.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class KsmNotationItem {

    private String notation;
    private String uid;
    private KsmFieldDataEnumType fieldDataType;
    private String fieldKey;
    private Boolean returnSingle;
    private Integer arrayIndex;
    private String dictKey;
    private String value;

    public KsmNotationItem(
            String notation,
            String uid,
            KsmFieldDataEnumType fieldDataType,
            String fieldKey,
            Boolean returnSingle,
            Integer arrayIndex,
            String dictKey) {

        if (returnSingle == null) {
            returnSingle = Boolean.TRUE;
        }
        if (arrayIndex == null) {
            arrayIndex = 0;
        }

        this.notation = notation;
        this.uid = uid;
        this.fieldDataType = fieldDataType;
        this.fieldKey = fieldKey;
        this.returnSingle = returnSingle;
        this.arrayIndex = arrayIndex;
        this.dictKey = dictKey;
    }

    public String getNotation() {
        return notation;
    }
    public String getUid() {
        return uid;
    }
    public KsmFieldDataEnumType getFieldDataType() {
        return fieldDataType;
    }
    public String getFieldKey() {
        return fieldKey;
    }
    public Boolean getReturnSingle() {
        return returnSingle;
    }
    public Integer getArrayIndex() {
        return arrayIndex;
    }
    public String getDictKey() {
        return dictKey;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public void setFieldDataType(KsmFieldDataEnumType fieldDataType) {
        this.fieldDataType = fieldDataType;
    }
    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }
    public void setReturnSingle(Boolean returnSingle) {
        this.returnSingle = returnSingle;
    }
    public void setArrayIndex(Integer arrayIndex) {
        this.arrayIndex = arrayIndex;
    }
    public void setDictKey(String dictKey) {
        this.dictKey = dictKey;
    }
    public void setValue(String value) {
        this.value = value;
    }

    // Make simple methods, so we can mock them for tests.
    public KeeperSecrets getSecrets(SecretsManagerOptions options, List<String> uid) {
        return SecretsManager.getSecrets(options, uid);
    }
    public byte[] downloadDataFile(KeeperFile file) {
        return downloadFile(file);
    }

    public String getValue(SecretsManagerOptions options) {

        if (this.value == null) {
            ArrayList<String> uid = new ArrayList<>();
            uid.add(this.getUid());

            KeeperSecrets secrets = this.getSecrets(options, uid);

            // Assuming the file is text. Putting binary data to a text field doesn't make
            // sense. This could be like a private key. Assume encoding is UTF_8.
            if ( this.getFieldDataType() == KsmFieldDataEnumType.FILE ) {
                KeeperFile file = Notation.getFile(secrets, this.getNotation());
                byte[] fileBytes = this.downloadDataFile(file);
                this.value = new String(fileBytes, StandardCharsets.UTF_8);
            }
            else {
                this.value = Notation.getValue(secrets, this.getNotation());
            }
        }

        return this.value;
    }
}
