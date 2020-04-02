/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.flume.sink.hbase.entity;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapValue {

    private List<Column>        columns           = new ArrayList<>();
    private Map<String, Object> primitiveValueMap = new HashMap<>();
    private Charset             charset;

    public MapValue(Charset charset){
        this.charset = charset;
    }

    public void put(byte[] family, byte[] qualifier, byte[] value, Object primitiveValue) {
        columns.add(new Column(family, qualifier, value, primitiveValue));
        primitiveValueMap.put(new String(qualifier, charset), primitiveValue);
    }

    public Object get(String field) {
        return primitiveValueMap.get(field);
    }

    public List<Column> getColumns() {
        return columns;
    }

    public static class Column {

        private byte[] family;

        private byte[] qualifier;

        private byte[] value;

        private Object primitiveValue;

        public Column(byte[] family, byte[] qualifier, byte[] value, Object primitiveValue){
            this.family = family;
            this.qualifier = qualifier;
            this.value = value;
            this.primitiveValue = primitiveValue;
        }

        public byte[] getFamily() {
            return family;
        }

        public void setFamily(byte[] family) {
            this.family = family;
        }

        public byte[] getQualifier() {
            return qualifier;
        }

        public void setQualifier(byte[] qualifier) {
            this.qualifier = qualifier;
        }

        public byte[] getValue() {
            return value;
        }

        public void setValue(byte[] value) {
            this.value = value;
        }

        public Object getPrimitiveValue() {
            return primitiveValue;
        }

        public void setPrimitiveValue(Object primitiveValue) {
            this.primitiveValue = primitiveValue;
        }

    }
}
