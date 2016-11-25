package com.xinthe.spax;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.xinthe.spax.utils.Constants;

/**
 * Created by xinthe on 15-Nov-16.
 */
public class DefaultSNSAWSLocationService {

    public int getServiceType()
    {
        return 1;
    }
    public Region getAWSRegion()
    {
        return Region.getRegion(Regions.US_WEST_2);
    }
    public String getURL()
    {
        return Constants.ARN_SNS_BEACONS;
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
