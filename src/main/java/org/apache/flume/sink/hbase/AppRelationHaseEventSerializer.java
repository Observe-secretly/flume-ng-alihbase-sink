/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.flume.sink.hbase;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.conf.ComponentConfiguration;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Row;
import org.apache.hadoop.hbase.util.Bytes;

import com.google.common.collect.Lists;
import com.timevale.cat.api.trace.DefaultTraceSegment;

import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class AppRelationHaseEventSerializer implements HbaseEventSerializer {

    /**
     * What charset to use when serializing into HBase's byte arrays
     */
    public static final String           CHARSET_CONFIG  = "charset";
    public static final String           CHARSET_DEFAULT = "UTF-8";

    /*
     * This is a nonce used in HBase row-keys, such that the same row-key never gets written more than once from within
     * this JVM.
     */
    protected static final AtomicInteger nonce           = new AtomicInteger(0);
    protected static String              randomKey       = RandomStringUtils.randomAlphanumeric(10);

    byte[]                               body;
    byte[]                               cf;
    private Charset                      charset;

    @Override
    public void initialize(Event event, byte[] columnFamily) {
        this.body = event.getBody();
        this.cf = columnFamily;
    }

    @Override
    public List<Row> getActions() {
        if (body == null || body.length == 0) {
            return Lists.newArrayList();
        }

        try {

            DefaultTraceSegment traceSegment = new DefaultTraceSegment();
            Schema<DefaultTraceSegment> schema = RuntimeSchema.getSchema(DefaultTraceSegment.class);
            ProtobufIOUtil.mergeFrom(body, traceSegment, schema);

            return translateToRow(traceSegment, body);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Lists.newArrayList();
    }

    public List<Row> translateToRow(DefaultTraceSegment traceSegment, byte[] body) {
        if (traceSegment == null || traceSegment.getRgts() == null || traceSegment.getRgts().size() == 0) {
            return Lists.newArrayList();
        }
        String rgtsId = traceSegment.getRgts().get(0);
        // 检测rgtsId是否符合要求
        if (rgtsId == null || rgtsId.length() <= 17) {
            System.out.println("格式异常RGTSID：" + rgtsId);
            return Lists.newArrayList();
        }

        Put put = new Put(getRowKey(rgtsId));
        put.addColumn(cf, Bytes.toBytes("trace_info"), body);

        return Lists.newArrayList(put);
    }

    protected byte[] getRowKey(String rgtsId) {
        return Bytes.toBytes(rgtsId.substring(rgtsId.length() - 17, rgtsId.length() - 4) + rgtsId);
    }

    @Override
    public List<Increment> getIncrements() {
        return Lists.newArrayList();
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Context context) {
        charset = Charset.forName(context.getString(CHARSET_CONFIG, CHARSET_DEFAULT));
    }

    @Override
    public void configure(ComponentConfiguration conf) {

    }
}
