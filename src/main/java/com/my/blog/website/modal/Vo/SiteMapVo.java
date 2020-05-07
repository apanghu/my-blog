package com.my.blog.website.modal.Vo;

import com.my.blog.website.utils.SiteMapUtils;
import org.thymeleaf.util.DateUtils;

import java.util.Date;
import java.util.Locale;

/**
 * @author Jesse-liu
 * @description: 网站地图entity
 * @date 2020/5/7 9:09
 */
public class SiteMapVo {
    /**
     * url https://www.xxx.com
     */
    private String loc;
    /**
     * 最后更新时间 yyyy-MM-dd
     */
    private Date lastmod;
    /**
     * 更新速度 always hourly daily weekly monthly yearly never
     */
    private String changefreq;
    /**
     * 权重 1.0 0.9 0.8
     */
    private String priority;

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public Date getLastmod() {
        return lastmod;
    }

    public void setLastmod(Date lastmod) {
        this.lastmod = lastmod;
    }

    public String getChangefreq() {
        return changefreq;
    }

    public void setChangefreq(String changefreq) {
        this.changefreq = changefreq;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public SiteMapVo() {

    }

    public SiteMapVo(String loc) {
        this.loc = loc;
        this.lastmod = new Date();
        this.changefreq = SiteMapUtils.CHANGEFREQ_ALWAYS;
        this.priority = "1.0";
    }

    public SiteMapVo(String loc, Date lastmod, String changefreq, String priority) {
        this.loc = loc;
        this.lastmod = lastmod;
        this.changefreq = changefreq;
        this.priority = priority;
    }

    @Override
    /** 重写 toString 适应xml格式 */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<url>");
        sb.append("<loc>" + loc + "</loc>");
        sb.append("<lastmod>" + DateUtils.format(lastmod, "yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE) + "</lastmod>");
        sb.append("<changefreq>" + changefreq + "</changefreq>");
        sb.append("<priority>" + priority + "</priority>");
        sb.append("</url>");
        return sb.toString();
    }
}
