package com.my.blog.website.utils;

import com.my.blog.website.constant.WebConst;
import com.my.blog.website.modal.Vo.ContentVo;
import com.my.blog.website.modal.Vo.SiteMapVo;
import com.my.blog.website.service.IContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Jesse-liu
 * @description: java生成sitemap网站地图工具类
 * @date 2020/5/7 10:25
 */
@Component
public class SiteMapUtils {

    public final static String BEGIN_DOC = "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">";
    public final static String END_DOC = "</urlset>";
    public final static String CHANGEFREQ_ALWAYS = "always";
    public final static String CHANGEFREQ_HOURLY = "hourly";
    public final static String CHANGEFREQ_DAILY = "daily";
    public final static String CHANGEFREQ_WEEKLY = "weekly";
    public final static String CHANGEFREQ_MONTHLY = "monthly";
    public final static String CHANGEFREQ_YEARLY = "yearly";
    public final static String CHANGEFREQ_NEVER = "never";

    @Autowired
    private IContentService contentsService;


    public String getBlogSiteMap() {
        StringBuffer sb = new StringBuffer();
        sb.append(BEGIN_DOC);
        sb.append(new SiteMapVo(WebConst.initConfig.get("site_url")));
        List<ContentVo> contentList = contentsService.findContentList();
        contentList.forEach(entity -> {
            sb.append(new SiteMapVo(Commons.permalink(entity), DateKit.dateFormat(Commons.fmtdate(entity.getModified(), "yyyy-MM-dd"), "yyyy-MM-dd"), CHANGEFREQ_MONTHLY, "0.9"));
        });
        sb.append(END_DOC);
        return sb.toString();
    }

    public String getBzSiteMap() {
        StringBuffer sb = new StringBuffer();
        sb.append(BEGIN_DOC);
        sb.append(new SiteMapVo(WebConst.initConfig.get("site_url")));
        sb.append(END_DOC);
        return sb.toString();
    }
}
