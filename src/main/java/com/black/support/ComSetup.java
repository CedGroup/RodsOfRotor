package com.black.support;

import java.io.Serializable;

/**
 * Created by Nick on 06.06.2016.
 */
public class ComSetup implements Serializable {
    private String port;
    private Integer bitRate;
    private Integer devAddress;
    private String path;

    public void setPort(Object port){
        this.port = port.toString();
    }

    public String getPort(){
        return port;
    }

    public void setBitRate(Object bitRate){
        this.bitRate = Integer.parseInt(bitRate.toString());
    }

    public Integer getBitRate(){
        return bitRate;
    }

    public void setDevAddress(Object devaAddress){
        this.devAddress = Integer.parseInt(devaAddress.toString());
    }

    public Integer getDevAddress(){
        return devAddress;
    }

    public void setPath(String path){
        this.path = path;
    }

    public String getPath(){
        return path;
    }
}
