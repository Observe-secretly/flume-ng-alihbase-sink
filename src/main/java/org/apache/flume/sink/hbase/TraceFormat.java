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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.timevale.cat.api.trace.DefaultTraceSegment;
import com.timevale.cat.api.trace.DefaultTraceSegmentRef;
import com.timevale.cat.api.trace.DefaultTracingSpan;
import com.timevale.cat.api.trace.LogDataEntity;
import com.timevale.cat.api.util.KeyValuePair;

public class TraceFormat {

    public static HashMap<String, Object> convertToMap(DefaultTraceSegment trace) throws Exception {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("applicationId", trace.getApplicationId());
        map.put("instanceId", trace.getApplicationInstanceId());
        map.put("ip", trace.getIp());
        map.put("traceSegmentId", trace.getTraceSegmentId());
        map.put("spans", convertSpansToListMap(trace.getSpans()));
        map.put("ignore", trace.isIgnore());
        map.put("singleSpanSegment", trace.isSingleSpanSegment());
        map.put("sizeLimited", trace.isSizeLimited());
        map.put("refs", convertRefsToListMap(trace.getRefs()));
        map.put("rgts", trace.getRgts());

        return map;
    }

    public static List<HashMap<String, Object>> convertRefsToListMap(List<DefaultTraceSegmentRef> refs) {
        if (refs == null || refs.size() == 0) {
            return null;
        }
        List<HashMap<String, Object>> result = new ArrayList<>();
        for (DefaultTraceSegmentRef ref : refs) {
            HashMap<String, Object> mapItem = new HashMap<String, Object>();
            mapItem.put("type", ref.getType());
            mapItem.put("entryApplicationInstanceId", ref.getEntryApplicationInstanceId());
            mapItem.put("entryOperationName", ref.getEntryOperationName());
            mapItem.put("parentApplicationInstanceId", ref.getParentApplicationInstanceId());
            mapItem.put("peerHost", ref.getPeerHost());
            mapItem.put("spanId", ref.getSpanId());
            mapItem.put("traceSegmentId", ref.getTraceSegmentId());
            result.add(mapItem);
        }
        return result;
    }

    public static List<HashMap<String, Object>> convertSpansToListMap(List<DefaultTracingSpan> spans) {
        if (spans == null || spans.size() == 0) {
            return null;
        }
        List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
        for (DefaultTracingSpan span : spans) {
            HashMap<String, Object> mapItem = new HashMap<String, Object>();
            mapItem.put("componentId", span.getComponentId());
            mapItem.put("entry", span.isEntry());
            mapItem.put("exit", span.isExit());
            mapItem.put("errorOccurred", span.isErrorOccurred());
            mapItem.put("endTime", span.getEndTime());
            mapItem.put("layer", span.getLayer() == null ? null : span.getLayer().name());
            mapItem.put("logs", convertLogsToListMap(span.getLogs()));
            mapItem.put("operationName", span.getOperationName());
            mapItem.put("parentSpanId", span.getParentSpanId());
            mapItem.put("spanId", span.getSpanId());
            mapItem.put("startTime", span.getStartTime());
            mapItem.put("tags", convertKVPairsToListMap(span.getTags()));
            result.add(mapItem);
        }
        return result;
    }

    public static List<HashMap<String, Object>> convertKVPairsToListMap(List<KeyValuePair> tags) {
        if (tags == null || tags.size() == 0) {
            return null;
        }
        List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
        for (KeyValuePair tag : tags) {
            result.add(convertKVPairToMap(tag));
        }
        return result;
    }

    public static HashMap<String, Object> convertKVPairToMap(KeyValuePair kvPari) {
        if (kvPari == null) {
            return null;
        }
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("key", kvPari.getKey());
        result.put("value", kvPari.getValue());
        return result;
    }

    public static List<HashMap<String, Object>> convertLogsToListMap(List<LogDataEntity> logs) {
        if (logs == null || logs.size() == 0) {
            return null;
        }
        List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
        for (LogDataEntity log : logs) {
            HashMap<String, Object> mapItem = new HashMap<String, Object>();
            mapItem.put("timestamp", log.getTimestamp());
            mapItem.put("logs", convertKVPairsToListMap(log.getLogs()));
            result.add(mapItem);
        }
        return result;
    }

    public static DefaultTracingSpan getEntrySpan(DefaultTraceSegment trace) {
        DefaultTracingSpan entrySpan = null;
        if (trace.getSpans() == null || trace.getSpans().size() == 0) {
            return null;
        }

        for (DefaultTracingSpan span : trace.getSpans()) {
            if (span.isEntry()) {
                entrySpan = span;
                break;
            }
        }

        // 找不到span则拿第一个(按照时间排序)
        if (entrySpan == null) {
            trace.getSpans().sort((a, b) -> {
                if (a.getStartTime() > b.getStartTime()) {
                    return 1;
                } else if (a.getStartTime() < b.getStartTime()) {
                    return -1;
                } else {
                    return 0;
                }
            });
            entrySpan = trace.getSpans().get(0);
        }

        return entrySpan;
    }

    public static DefaultTracingSpan getParentSpan(DefaultTraceSegment trace, int spanId) {
        DefaultTracingSpan parentSpan = null;
        for (DefaultTracingSpan span : trace.getSpans()) {
            if (span.getParentSpanId() == spanId) {
                parentSpan = span;
                break;
            }
        }

        // 找不到span则拿第一个(按照时间排序)
        if (parentSpan == null) {
            trace.getSpans().sort((a, b) -> {
                if (a.getStartTime() > b.getStartTime()) {
                    return 1;
                } else if (a.getStartTime() < b.getStartTime()) {
                    return -1;
                } else {
                    return 0;
                }
            });
            parentSpan = trace.getSpans().get(0);
        }

        return parentSpan;
    }
}
