package com.xinthe.spax;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.xinthe.spax.utils.Constants;

import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);


        //MessagingController mc = new MessagingController(this,this);
    /*    BeaconCollector bc =
                new BeaconCollector(this.getContext(), Constants.XINTHE_UUID, new DefaultSNSAWSLocationService());
        bc.startUpload(); */
    }
}