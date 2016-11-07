package com.jiedu.project.lovefamily.mode;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/6.
 */
public class PayStyleMode {
        private String name;
        private String imgUrl;
        private String remark;
        private String id;
        private boolean isChecked;




        private ArrayList<ServiceMode> list;

        public   boolean  getIsChecked(){
                return  isChecked;
        }
        public void setIsChecked(boolean isChecked) {
                this.isChecked = isChecked;
        }
        public void setId(String id) {
                this.id = id;
        }

        public void setImgUrl(String imgUrl) {
                this.imgUrl = imgUrl;
        }

        public void setList(ArrayList<ServiceMode> list) {
                this.list = list;
        }

        public void setName(String name) {
                this.name = name;
        }

        public void setRemark(String remark) {
                this.remark = remark;
        }

        public String getId() {
                return id;
        }

        public ArrayList<ServiceMode> getList() {
                return list;
        }

        public String getImgUrl() {
                return imgUrl;
        }

        public String getName() {
                return name;
        }

        public String getRemark() {
                return remark;
        }

}
