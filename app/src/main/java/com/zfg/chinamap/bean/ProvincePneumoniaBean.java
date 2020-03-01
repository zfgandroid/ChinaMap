package com.zfg.chinamap.bean;

/**
 * @author zhongfanggui
 * 各省肺炎数据
 */
public class ProvincePneumoniaBean {

    //省份名称
    private String provinceShortName;
    //确诊人数
    private int confirmedCount;
    //治愈人数
    private int curedCount;
    //死亡人数
    private int deadCount;

    public ProvincePneumoniaBean(String provinceShortName, int confirmedCount, int curedCount, int deadCount) {
        this.provinceShortName = provinceShortName;
        this.confirmedCount = confirmedCount;
        this.curedCount = curedCount;
        this.deadCount = deadCount;
    }

    public String getProvinceShortName() {
        return provinceShortName;
    }

    public void setProvinceShortName(String provinceShortName) {
        this.provinceShortName = provinceShortName;
    }

    public int getConfirmedCount() {
        return confirmedCount;
    }

    public void setConfirmedCount(int confirmedCount) {
        this.confirmedCount = confirmedCount;
    }

    public int getCuredCount() {
        return curedCount;
    }

    public void setCuredCount(int curedCount) {
        this.curedCount = curedCount;
    }

    public int getDeadCount() {
        return deadCount;
    }

    public void setDeadCount(int deadCount) {
        this.deadCount = deadCount;
    }
}
