package org.apache.flume.sink.hbase.entity;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;

public class SimpleTraceInfo implements Serializable {

    private static final long serialVersionUID = 6428065842478019008L;

    private String            segmentId;

    private String            parentSegmentId;

    private String            applicationId;

    private String            ip;

    private String            serviceName;

    public SimpleTraceInfo(String segmentId, String parentSegmentId, String applicationId, String ip,
                           String serviceName){
        this.segmentId = segmentId;
        this.parentSegmentId = parentSegmentId;
        this.applicationId = applicationId;
        this.ip = ip;
        this.serviceName = serviceName;
    }

    public SimpleTraceInfo(){
    }

    public String getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(String segmentId) {
        this.segmentId = segmentId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getParentSegmentId() {
        return parentSegmentId;
    }

    public void setParentSegmentId(String parentSegmentId) {
        this.parentSegmentId = parentSegmentId;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
