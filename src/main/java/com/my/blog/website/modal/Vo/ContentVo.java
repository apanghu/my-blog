package com.my.blog.website.modal.Vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import java.io.Serializable;

/**
 * @author easyexcel 使用Java模型的方式需要继承 BaseRowModel ，字段上使用 @ExcelProperty 注解，
 * 注解中 value 属性指定字段名，index属性指定字段排序。
 */
public class ContentVo extends BaseRowModel implements Serializable {
    /**
     * post表主键
     */
    @ExcelIgnore
    private Integer cid;

    /**
     * 内容标题
     */
    @ExcelProperty(value = "内容标题", index = 0)
    private String title;

    /**
     * 内容缩略名
     */
    @ExcelIgnore
    private String slug;

    /**
     * 内容生成时的GMT unix时间戳
     */
    @ExcelIgnore
    private Integer created;

    /**
     * 内容更改时的GMT unix时间戳
     */
    @ExcelIgnore
    private Integer modified;

    /**
     * 内容所属用户id
     */
    @ExcelIgnore
    private Integer authorId;

    /**
     * 内容所属用户id
     */
    @ExcelIgnore
    private String author;
    /**
     * 内容类别
     */
    @ExcelIgnore
    private String type;

    /**
     * 内容状态
     */
    @ExcelIgnore
    private String status;

    /**
     * 标签列表
     */
    @ExcelIgnore
    private String tags;

    /**
     * 分类列表
     */
    @ExcelIgnore
    private String categories;

    /**
     * 点击次数
     */
    @ExcelIgnore
    private Integer hits;

    /**
     * 内容所属评论数
     */
    @ExcelIgnore
    private Integer commentsNum;

    /**
     * 是否允许评论
     */
    @ExcelIgnore
    private Boolean allowComment;

    /**
     * 是否允许ping
     */
    @ExcelIgnore
    private Boolean allowPing;

    /**
     * 允许出现在聚合中
     */
    @ExcelIgnore
    private Boolean allowFeed;

    /**
     * 内容文字
     */
    @ExcelProperty(value = "内容文字", index = 1)
    private String content;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    private static final long serialVersionUID = 1L;

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public Integer getModified() {
        return modified;
    }

    public void setModified(Integer modified) {
        this.modified = modified;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public Integer getHits() {
        return hits;
    }

    public void setHits(Integer hits) {
        this.hits = hits;
    }

    public Integer getCommentsNum() {
        return commentsNum;
    }

    public void setCommentsNum(Integer commentsNum) {
        this.commentsNum = commentsNum;
    }

    public Boolean getAllowComment() {
        return allowComment;
    }

    public void setAllowComment(Boolean allowComment) {
        this.allowComment = allowComment;
    }

    public Boolean getAllowPing() {
        return allowPing;
    }

    public void setAllowPing(Boolean allowPing) {
        this.allowPing = allowPing;
    }

    public Boolean getAllowFeed() {
        return allowFeed;
    }

    public void setAllowFeed(Boolean allowFeed) {
        this.allowFeed = allowFeed;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ContentVo() {
    }

}