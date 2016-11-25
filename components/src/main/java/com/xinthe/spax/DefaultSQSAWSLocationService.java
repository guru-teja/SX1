package com.xinthe.spax;

import android.location.Location;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.xinthe.spax.utils.Constants;

/**
 * Created by xinthe on 15-Nov-16.
 */

public class DefaultSQSAWSLocationService {
    /* 1 = SQS, 2= SNS, 3=HTTP */
    public int getServiceType()
    {
        return 1;
    }
    public Regions getAWSRegion() {return Regions.US_WEST_2; }
    public String getURL()
    {
        return Constants.SQS_URL;
    }
    public  String getAccessID()
    {
        return Constants.ACCESS_ID;
    }
    public String getSecretKey()
    {
        return Constants.SECRET_KEY;
    }
}
