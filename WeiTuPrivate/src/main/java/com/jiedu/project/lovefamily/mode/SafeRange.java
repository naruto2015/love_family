package com.jiedu.project.lovefamily.mode;

/**
 * Created by Administrator on 2016/3/24.
 */
public class SafeRange {
    public double lat;
    public double lon;
    public String address;
    public int radius;
    public String refId;
    public String title;
    public String id;

    public void init(double lat,double lon,String address,int radius,String refId,String title,String id){
        this.lat=lat;
        this.lon=lon;
        this.address=address;
        this.radius=radius;
        this.refId=refId;
        this.title=title;
        this.id=id;
    }

}
