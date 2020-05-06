package com.my.blog.website.controller;

import com.github.pagehelper.PageInfo;
import com.my.blog.website.constant.WebConst;
import com.my.blog.website.dto.ErrorCode;
import com.my.blog.website.dto.MetaDto;
import com.my.blog.website.dto.Types;
import com.my.blog.website.exception.TipException;
import com.my.blog.website.modal.Bo.ArchiveBo;
import com.my.blog.website.modal.Bo.CommentBo;
import com.my.blog.website.modal.Bo.RestResponseBo;
import com.my.blog.website.modal.Vo.*;
import com.my.blog.website.service.ICommentService;
import com.my.blog.website.service.IContentService;
import com.my.blog.website.service.IMetaService;
import com.my.blog.website.service.ISiteService;
import com.my.blog.website.utils.*;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页
 * Created by Administrator on 2017/3/8 008.
 */
@Controller
public class IndexController extends BaseController {

    @Resource
    private IContentService contentService;

    @Resource
    private ICommentService commentService;

    @Resource
    private IMetaService metaService;

    @Resource
    private ISiteService siteService;


    /**
     * @param request
     * @return : void
     * @author Jesse-liu
     * @date 2020/4/23
     * @description: 注解表示每次请求都将先到达此方法
     **/
    @ModelAttribute
    public void list(HttpServletRequest request) {
        PageInfo<ContentVo> articles = contentService.getContents(1, 5);
        request.setAttribute("newArticles", articles.getList());
        //获取最新评论
        PageInfo<CommentBo> commentsPaginator = commentService.getComments(null, 1, 5);
        request.setAttribute("newComments", commentsPaginator.getList());
    }

    /**
     * 首页
     *
     * @return
     */
    @GetMapping(value = {"/", "index"})
    public String index(HttpServletRequest request, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        //获取最新文章
        return this.index(request, 1, limit);
    }

    /**
     * 首页分页
     *
     * @param request request
     * @param p       第几页
     * @param limit   每页大小
     * @return 主页
     */
    @GetMapping(value = "page/{p}")
    public String index(HttpServletRequest request, @PathVariable int p, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        p = p < 0 || p > WebConst.MAX_PAGE ? 1 : p;
        PageInfo<ContentVo> articles = contentService.getContents(p, limit);
        request.setAttribute("articles", articles);
        if (p > 1) {
            this.title(request, "第" + p + "页");
        }
        return this.render("index");
    }

    /**
     * 文章页
     *
     * @param request 请求
     * @param cid     文章主键
     * @return
     */
    @GetMapping(value = {"article/{cid}", "article/{cid}.html"})
    public String getArticle(HttpServletRequest request, @PathVariable String cid) {
        ContentVo contents = contentService.getContents(cid);
        if (null == contents || "draft".equals(contents.getStatus())) {
            return this.render_404();
        }
        request.setAttribute("article", contents);
        request.setAttribute("is_post", true);
        completeArticle(request, contents);
        updateArticleHit(contents.getCid(), contents.getHits());
        return this.render("post");
    }

    /**
     * 获取作者下的所有文章页
     *
     * @param request 请求
     * @param author
     * @return
     */
    @GetMapping(value = "author/{author}")
    public String getAuthorArticle(HttpServletRequest request, @PathVariable("author") String author) {
        List<ArchiveBo> archives = siteService.getAuthorArticle(author);
        request.setAttribute("archives", archives);
        return this.render("archives");
    }

    /**
     * 文章页(预览)
     *
     * @param request 请求
     * @param cid     文章主键
     * @return
     */
    @GetMapping(value = {"article/{cid}/preview", "article/{cid}.html"})
    public String articlePreview(HttpServletRequest request, @PathVariable String cid) {
        ContentVo contents = contentService.getContents(cid);
        if (null == contents) {
            return this.render_404();
        }
        request.setAttribute("article", contents);
        request.setAttribute("is_post", true);
        completeArticle(request, contents);
        updateArticleHit(contents.getCid(), contents.getHits());
        return this.render("post");
    }

    /**
     * 抽取公共方法
     *
     * @param request
     * @param contents
     */
    private void completeArticle(HttpServletRequest request, ContentVo contents) {
        if (contents.getAllowComment()) {
            String cp = request.getParameter("cp");
            if (StringUtils.isBlank(cp)) {
                cp = "1";
            }
            request.setAttribute("cp", cp);
            PageInfo<CommentBo> commentsPaginator = commentService.getComments(contents.getCid(), Integer.parseInt(cp), 6);
            if (commentsPaginator.getList() != null) {
                commentsPaginator.setList(listToTree(commentsPaginator.getList()));
            }
            request.setAttribute("comments", commentsPaginator);
        }
    }

    public static List<CommentBo> listToTree(List<CommentBo> list) {
        //用递归找子。
        List<CommentBo> treeList = new ArrayList<CommentBo>();
        for (CommentBo tree : list) {
            //根目录的parentId为0
            if (tree.getParent() == 0) {
                treeList.add(findChildren(tree, list));
            }
        }
        return treeList;
    }

    private static CommentBo findChildren(CommentBo tree, List<CommentBo> list) {
        for (CommentBo node : list) {
            if (node.getParent().longValue() == tree.getCoid().longValue()) {
                if (tree.getChildren() == null) {
                    tree.setChildren(new ArrayList<CommentVo>());
                }
                tree.getChildren().add(findChildren(node, list));
            }
        }
        return tree;
    }

    /**
     * 注销
     *
     * @param session
     * @param response
     */
    @RequestMapping("logout")
    public void logout(HttpSession session, HttpServletResponse response) {
        TaleUtils.logout(session, response);
    }

    /**
     * 评论操作
     */
    @PostMapping(value = "comment")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo comment(HttpServletRequest request, HttpServletResponse response,
                                  CommentVo commentVo,
                                  @RequestParam String _csrf_token) {

        String ref = request.getHeader("Referer");
        if (StringUtils.isBlank(ref) || StringUtils.isBlank(_csrf_token)) {
            return RestResponseBo.fail(ErrorCode.BAD_REQUEST);
        }

        String token = cache.hget(Types.CSRF_TOKEN.getType(), _csrf_token);
        if (StringUtils.isBlank(token)) {
            return RestResponseBo.fail(ErrorCode.BAD_REQUEST);
        }

        if (null == commentVo.getCid() || StringUtils.isBlank(commentVo.getContent())
                || StringUtils.isBlank(commentVo.getAuthor())
                || StringUtils.isBlank(commentVo.getMail())
                || StringUtils.isBlank(commentVo.getUrl())
        ) {
            return RestResponseBo.fail("请输入完整后评论");
        }

        if (StringUtils.isNotBlank(commentVo.getAuthor()) && commentVo.getAuthor().length() > 50) {
            return RestResponseBo.fail("姓名过长");
        }

        if (StringUtils.isNotBlank(commentVo.getMail()) && !TaleUtils.isEmail(commentVo.getMail())) {
            return RestResponseBo.fail("请输入正确的邮箱格式");
        }

        if (StringUtils.isNotBlank(commentVo.getUrl()) && !PatternKit.isURL(commentVo.getUrl())) {
            return RestResponseBo.fail("请输入正确的URL格式");
        }

        if (commentVo.getContent().length() > 200) {
            return RestResponseBo.fail("请输入200个字符以内的评论");
        }

        String val = IPKit.getIpAddrByRequest(request) + ":" + commentVo.getCid();
        Integer count = cache.hget(Types.COMMENTS_FREQUENCY.getType(), val);
        if (null != count && count > 0) {
            return RestResponseBo.fail("您发表评论太快了，请过会再试");
        }

        commentVo.setAuthor(TaleUtils.cleanXSS(commentVo.getAuthor()));
        commentVo.setContent(TaleUtils.cleanXSS(commentVo.getContent()));

        commentVo.setAuthor(EmojiParser.parseToAliases(commentVo.getAuthor()));
        commentVo.setContent(EmojiParser.parseToAliases(commentVo.getContent()));


        CommentVo comments = new CommentVo();
        comments.setAuthor(commentVo.getAuthor());
        comments.setCid(commentVo.getCid());
        comments.setIp(request.getRemoteAddr());
        comments.setUrl(commentVo.getUrl());
        comments.setContent(commentVo.getContent());
        comments.setMail(commentVo.getMail());
        comments.setParent(commentVo.getCoid());
        try {
            commentService.insertComment(comments);
            cookie("tale_remember_author", URLEncoder.encode(commentVo.getAuthor(), "UTF-8"), 7 * 24 * 60 * 60, response);
            cookie("tale_remember_mail", URLEncoder.encode(commentVo.getMail(), "UTF-8"), 7 * 24 * 60 * 60, response);
            if (StringUtils.isNotBlank(commentVo.getUrl())) {
                cookie("tale_remember_url", URLEncoder.encode(commentVo.getUrl(), "UTF-8"), 7 * 24 * 60 * 60, response);
            }
            // 设置对每个文章1分钟可以评论一次
            cache.hset(Types.COMMENTS_FREQUENCY.getType(), val, 1, 60);
            //回复评论------>邮件通知
            String email_notification = WebConst.initConfig.get("email_notification");
            if (StringUtils.isNotBlank(email_notification) && "1".equalsIgnoreCase(email_notification)) {
                if (comments.getParent() != null && comments.getParent() != 0) {
                    CommentVo commentVo1 = commentService.getCommentById(comments.getParent());
                    replyComment(comments, commentVo1.getMail(), comments.getCid().toString());
                } else {
                    //回复文章
                    replyArticle(comments, comments.getCid().toString());
                }
            }
            return RestResponseBo.ok();
        } catch (Exception e) {
            String msg = "评论发布失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
    }


    /**
     * 分类页
     *
     * @return
     */
    @GetMapping(value = "category/{keyword}")
    public String categories(HttpServletRequest request, @PathVariable String keyword, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        return this.categories(request, keyword, 1, limit);
    }

    @GetMapping(value = "category/{keyword}/{page}")
    public String categories(HttpServletRequest request, @PathVariable String keyword,
                             @PathVariable int page, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        page = page < 0 || page > WebConst.MAX_PAGE ? 1 : page;
        MetaDto metaDto = metaService.getMeta(Types.CATEGORY.getType(), keyword);
        if (null == metaDto) {
            return this.render_404();
        }

        PageInfo<ContentVo> contentsPaginator = contentService.getArticles(metaDto.getMid(), page, limit);

        request.setAttribute("articles", contentsPaginator);
        request.setAttribute("meta", metaDto);
        request.setAttribute("type", "分类");
        request.setAttribute("keyword", keyword);

        return this.render("page-category");
    }


    /**
     * 归档页
     *
     * @return
     */
    @GetMapping(value = "archives")
    public String archives(HttpServletRequest request) {
        List<ArchiveBo> archives = siteService.getArchives();
        request.setAttribute("archives", archives);
        return this.render("archives");
    }

    /**
     * 友链页
     *
     * @return
     */
    @GetMapping(value = "links")
    public String links(HttpServletRequest request) {
        List<MetaVo> links = metaService.getMetas(Types.LINK.getType());
        ContentVo contents = contentService.getContents("links");
        request.setAttribute("is_post", true);
        request.setAttribute("article", contents);
        request.setAttribute("links", links);
        return this.render("links");
    }

    /**
     * 自定义页面,如关于的页面
     */
    @GetMapping(value = "/{pagename}")
    public String page(@PathVariable String pagename, HttpServletRequest request) {
        ContentVo contents = contentService.getContents(pagename);
        if (null == contents) {
            return this.render_404();
        }
        if (contents.getAllowComment()) {
            String cp = request.getParameter("cp");
            if (StringUtils.isBlank(cp)) {
                cp = "1";
            }
            PageInfo<CommentBo> commentsPaginator = commentService.getComments(contents.getCid(), Integer.parseInt(cp), 6);
            request.setAttribute("comments", commentsPaginator);
        }
        request.setAttribute("article", contents);
        request.setAttribute("is_post", true);
        updateArticleHit(contents.getCid(), contents.getHits());
        return this.render("page");
    }

    /**
     * 搜索页
     *
     * @param keyword
     * @return
     */
    @GetMapping(value = "search/{keyword}")
    public String search(HttpServletRequest request, @PathVariable String keyword, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        return this.search(request, keyword, 1, limit);
    }

    @GetMapping(value = "search/{keyword}/{page}")
    public String search(HttpServletRequest request, @PathVariable String keyword, @PathVariable int page, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        page = page < 0 || page > WebConst.MAX_PAGE ? 1 : page;
        PageInfo<ContentVo> articles = contentService.getArticles(keyword, page, limit);
        request.setAttribute("articles", articles);
        request.setAttribute("type", "搜索");
        request.setAttribute("keyword", keyword);
        return this.render("page-category");
    }

    /**
     * 更新文章的点击率
     *
     * @param cid
     * @param chits
     */
    @Transactional(rollbackFor = TipException.class)
    private void updateArticleHit(Integer cid, Integer chits) {
        Integer hits = cache.hget("article", "hits");
        if (chits == null) {
            chits = 0;
        }
        hits = null == hits ? 1 : hits + 1;
        if (hits >= WebConst.HIT_EXCEED) {
            ContentVo temp = new ContentVo();
            temp.setCid(cid);
            temp.setHits(chits + hits);
            contentService.updateContentByCid(temp);
            cache.hset("article", "hits", 1);
        } else {
            cache.hset("article", "hits", hits);
        }
    }

    /**
     * 标签页
     *
     * @param name
     * @return
     */
    @GetMapping(value = "tag/{name}")
    public String tags(HttpServletRequest request, @PathVariable String name, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        return this.tags(request, name, 1, limit);
    }

    /**
     * 标签分页
     *
     * @param request
     * @param name
     * @param page
     * @param limit
     * @return
     */
    @GetMapping(value = "tag/{name}/{page}")
    public String tags(HttpServletRequest request, @PathVariable String name, @PathVariable int page, @RequestParam(value = "limit", defaultValue = "12") int limit) {

        page = page < 0 || page > WebConst.MAX_PAGE ? 1 : page;
//        对于空格的特殊处理
        name = name.replaceAll("\\+", " ");
        MetaDto metaDto = metaService.getMeta(Types.TAG.getType(), name);
        if (null == metaDto) {
            return this.render_404();
        }

        PageInfo<ContentVo> contentsPaginator = contentService.getArticles(metaDto.getMid(), page, limit);
        request.setAttribute("articles", contentsPaginator);
        request.setAttribute("meta", metaDto);
        request.setAttribute("type", "标签");
        request.setAttribute("keyword", name);

        return this.render("page-category");
    }

    /**
     * 设置cookie
     *
     * @param name
     * @param value
     * @param maxAge
     * @param response
     */
    private void cookie(String name, String value, int maxAge, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(false);
        response.addCookie(cookie);
    }


    /**
     * @param commentVo 评论实体
     * @param cid       文章cid
     * @return : void
     * @author Jesse-liu
     * @date 2020/4/21
     * @description: 回复文章评论邮件发送方法
     **/
    public void replyArticle(CommentVo commentVo, String cid) {
        ContentVo contents = contentService.getContents(cid);
        MailBoxVo mailBoxVo = (MailBoxVo) MapCache.single().get("mailBoxVo");
        MailVo mailVo = new MailVo(mailBoxVo.getReceiver(),
                "文章：" +
                        contents.getTitle() + "  有一条新评论",
                "文章标题：" +
                        contents.getTitle() +
                        "\r\n文章链接：" +
                        Commons.permalink(Integer.parseInt(cid), null) +
                        "\r\n评论人：" + commentVo.getAuthor() +
                        "\r\n评论人邮箱：" + commentVo.getMail() +
                        "\r\n评论人网址：" + commentVo.getUrl() +
                        "\r\n评论内容：" + commentVo.getContent());
        SendMailManager.addMail(mailVo);
    }

    /**
     * @param commentVo 评论实体
     * @param cid       文章cid
     * @return : void
     * @author Jesse-liu
     * @date 2020/4/21
     * @description: 回复评论邮件发送方法
     **/
    public void replyComment(CommentVo commentVo, String receiver, String cid) {
        ContentVo contents = contentService.getContents(cid);
        MailVo mailVo = new MailVo(receiver,
                "文章：" +
                        contents.getTitle() + " 有人回复了您的评论",
                "文章标题：" +
                        contents.getTitle() +
                        "\r\n文章链接：" +
                        Commons.permalink(Integer.parseInt(cid), null) +
                        "\r\n评论人：" + commentVo.getAuthor() +
                        "\r\n评论人邮箱：" + commentVo.getMail() +
                        "\r\n评论人网址：" + commentVo.getUrl() +
                        "\r\n评论内容：" + commentVo.getContent());
        SendMailManager.addMail(mailVo);
    }

    @GetMapping("search")
    public String search(HttpServletRequest request) {
        List<MetaDto> metaList = metaService.getMetaList(Types.TAG.getType(), null, 10);
        request.setAttribute("metaList", metaList);
        return this.render("search");
    }
}
