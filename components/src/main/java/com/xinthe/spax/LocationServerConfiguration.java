package com.xinthe.spax;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

import java.io.Serializable;

/**
 * Created by xinthe on 15-Nov-16.
 */
public class LocationServerConfiguration implements Serializable
{
    public ServerTypes ServerType; /* 1 = SQS, 2= SNS, 3=HTTP */
    public Regions AWSRegion; /* In case of AWS SQS, SNS */
    public String URL;
    public String AccessID;
    public String SecretKey;

    public enum ServerTypes
    {
        SQS,
        SNS,
        HTTP,
        FILESYSTEM
    }

    public LocationServerConfiguration(LocationServerConfiguration.ServerTypes serverType, Regions awsRegion, String url,String accessID, String secretKey )
    {
        this.ServerType = serverType;
        this.AWSRegion = awsRegion;
        this.URL = url;
        this.AccessID = accessID;
        this.SecretKey= secretKey;
    }
}