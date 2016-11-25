package com.xinthe.spaxtest;

/**
 * Created by xinthe on 18-Nov-16.
 */
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class DataModel  {
//    public class Album {
         String ID;
         String Title;
         String Message;
         String Attachement;
         String Couponcode;
         String Validity;


//        public DataModel() {
//        }

        public DataModel(String ID, String Title, String Message, String Attachement, String Couponcode,String Validity) {
            this.ID = ID;
            this.Title = Title;
            this.Message = Message;
            this.Attachement = Attachement;
            this.Couponcode = Couponcode;
            this.Validity = Validity;
        }

        public String getID(String ID) {
            return ID;
        }
        public void setID(String ID)
        {
            this.ID = ID;
        }


        public String getTitle() {
            return Title;
        }

        public void setTitle(String Title) {
            this.Title = Title;
        }

        public String getMessage() {
            return Message;
        }

        public void setMessage(String Message) {
            this.Message = Message;
        }


        public String getAttachement() {
            return Attachement;
        }

        public void setAttachement(String Attachement) {
            this.Attachement = Attachement;
        }


         public String getCouponcode() {
            return Couponcode;
        }

        public void setCouponcode(String Couponcode) {
            this.Couponcode = Couponcode;
        }


        public String getValidity() {
            return Validity;
        }

        public void setValidity(String Validity) {
            this.Validity = Validity;
        }



    }