package com.example.cpd.model;

public class Activity {
    //FIELD NAME MUST MATCH WITH FIELD VALUE PRESENT IN FIREBASE DATABASE
    private String Activity_Name;
    private String Activity_Date;
    private String Activity_Description;
    private String Activity_Mins;
    private String Activity_Hours;
    private String Activity_Ref1;
    private String Activity_Ref2;
    private String Activity_Ref3;
    private String Activity_Ref4;
    private String Activity_Type;
    private String Image_URL;
    private boolean In_Audit;

    //EMPTY CONSTRUCTOR
    public Activity(){

    }

    //CONSTRUCTOR WITH ALL ATTRIBUTES
    public Activity(String name, String date, String hours, String minutes, String description, String ref1, String ref2, String ref3, String ref4, String type, String image_URL, boolean In_Audit){
        this.Activity_Name = Activity_Name;
        this.Activity_Date = Activity_Date;
        this.Activity_Hours = Activity_Hours;
        this.Activity_Mins = Activity_Mins;
        this.Activity_Description = Activity_Description;
        this.Activity_Ref1 = Activity_Ref1;
        this.Activity_Ref2 = Activity_Ref2;
        this.Activity_Ref3 = Activity_Ref3;
        this.Activity_Ref4 = Activity_Ref4;
        this.Activity_Type = Activity_Type;
        this.Image_URL = Image_URL;
        this.In_Audit = false;
    }

    public String getActivity_Name() {
        return Activity_Name;
    }

    public void setActivity_Name(String activity_Name) {
        Activity_Name = activity_Name;
    }

    public String getActivity_Date() {
        return Activity_Date;
    }

    public void setActivity_Date(String activity_Date) {
        Activity_Date = activity_Date;
    }

    public String getActivity_Description() {
        return Activity_Description;
    }

    public void setActivity_Description(String activity_Description) {
        Activity_Description = activity_Description;
    }

    public String getActivity_Mins() {
        return Activity_Mins;
    }

    public void setActivity_Mins(String activity_Mins) {
        Activity_Mins = activity_Mins;
    }

    public String getActivity_Hours() {
        return Activity_Hours;
    }

    public void setActivity_Hours(String activity_Hours) {
        Activity_Hours = activity_Hours;
    }

    public String getActivity_Ref1() {
        return Activity_Ref1;
    }

    public void setActivity_Ref1(String activity_Ref1) {
        Activity_Ref1 = activity_Ref1;
    }

    public String getActivity_Ref2() {
        return Activity_Ref2;
    }

    public void setActivity_Ref2(String activity_Ref2) {
        Activity_Ref2 = activity_Ref2;
    }

    public String getActivity_Ref3() {
        return Activity_Ref3;
    }

    public void setActivity_Ref3(String activity_Ref3) {
        Activity_Ref3 = activity_Ref3;
    }

    public String getActivity_Ref4() {
        return Activity_Ref4;
    }

    public void setActivity_Ref4(String activity_Ref4) {
        Activity_Ref4 = activity_Ref4;
    }

    public String getActivity_Type() {
        return Activity_Type;
    }

    public void setActivity_Type(String activity_Type) {
        Activity_Type = activity_Type;
    }

    public String getImage_URL() {
        return Image_URL;
    }

    public void setImage_URL(String image_URL) {
        Image_URL = image_URL;
    }

    public boolean isIn_Audit() {
        return In_Audit;
    }

    public void setIn_Audit(boolean in_Audit) {
        In_Audit = in_Audit;
    }

}
