package com.xinthe.spax.services;

import com.xinthe.spax.BeaconCollector;
import com.xinthe.spax.LocationServerConfiguration;

import java.io.Serializable;

/**
 * Created by xinthe on 15-Nov-16.
 */
public class BeaconScannerSettings implements Serializable
{
    public String beaconUUID;
    public int aggregationType;
    public boolean computeLocation;
    public LocationServerConfiguration locationServerConfig;
    public int locationAlgorithmType;

    public BeaconScannerSettings(String beaconUUID, LocationServerConfiguration locationServerConfig, int aggregationType,
                                      boolean computeLocation, int locationAlgorithm )
    {
        this.aggregationType = aggregationType;
        this.beaconUUID = beaconUUID;
        this.locationServerConfig = locationServerConfig;
        this.computeLocation = computeLocation;
        this.locationAlgorithmType = locationAlgorithm;
    }
}