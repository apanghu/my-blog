package com.my.blog.website.controller.admin;


import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageInfo;
import com.my.blog.website.controller.BaseController;
import com.my.blog.website.dto.LogActions;
import com.my.blog.website.dto.Types;
import com.my.blog.website.exception.TipException;
import com.my.blog.website.modal.Bo.RestResponseBo;
import com.my.blog.website.modal.Vo.ContentVo;
import com.my.blog.website.modal.Vo.ContentVoExample;
import com.my.blog.website.modal.Vo.MetaVo;
import com.my.blog.website.modal.Vo.UserVo;
import com.my.blog.website.service.IContentService;
import com.my.blog.website.service.ILogService;
import com.my.blog.website.service.IMetaService;
import com.my.blog.website.utils.FileUtils;
import com.my.blog.website.utils.TaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by 13 on 2017/2/21.
 */
@Controller
@RequestMapping("/admin/article")
@Transactional(rollbackFor = TipException.class)
public class ArticleController extends BaseController {


    @Resource
    private IContentService contentsService;

    @Resource
    private IMetaService metasService;

    @Resource
    private ILogService logService;

    /**
     * 文章列表
     *
     * @param page
     * @param limit
     * @param request
     * @return
     */
    @GetMapping(value = "")
    public String index(@RequestParam(value = "page", defaultValue = "1") int page,
                        @RequestParam(value = "limit", defaultValue = "15") int limit, HttpServletRequest request) {
        ContentVoExample contentVoExample = new ContentVoExample();
        contentVoExample.setOrderByClause("created desc");
        contentVoExample.createCriteria().andTypeEqualTo(Types.ARTICLE.getType());
        PageInfo<ContentVo> contentsPaginator = contentsService.getArticlesWithpage(contentVoExample, page, limit);
        request.setAttribute("articles", contentsPaginator);
        return "admin/article_list";
    }

    /**
     * 文章发表
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/publish")
    public String newArticle(HttpServletRequest request) {
        List<MetaVo> categories = metasService.getMetas(Types.CATEGORY.getType());
        request.setAttribute("categories", categories);
        return "admin/article_edit";
    }

    /**
     * 文章编辑
     *
     * @param cid
     * @param request
     * @return
     */
    @GetMapping(value = "/{cid}")
    public String editArticle(@PathVariable String cid, HttpServletRequest request) {
        ContentVo contents = contentsService.getContents(cid);
        request.setAttribute("contents", contents);
        List<MetaVo> categories = metasService.getMetas(Types.CATEGORY.getType());
        request.setAttribute("categories", categories);
        request.setAttribute("active", "article");
        return "admin/article_edit";
    }

    /**
     * 文章发表
     *
     * @param contents
     * @param request
     * @return
     */
    @PostMapping(value = "/publish")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo publishArticle(ContentVo contents, HttpServletRequest request) {
        UserVo users = this.user(request);
        contents.setAuthorId(users.getUid());
        contents.setType(Types.ARTICLE.getType());
        if (StringUtils.isBlank(contents.getCategories())) {
            contents.setCategories("默认分类");
        }
        try {
            contentsService.publish(contents);
        } catch (Exception e) {
            String msg = "文章发布失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }

    /**
     * 文章更新
     *
     * @param contents
     * @param request
     * @return
     */
    @PostMapping(value = "/modify")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo modifyArticle(ContentVo contents, HttpServletRequest request) {
        UserVo users = this.user(request);
        contents.setAuthorId(users.getUid());
        contents.setType(Types.ARTICLE.getType());
        try {
            contentsService.updateArticle(contents);
        } catch (Exception e) {
            String msg = "文章编辑失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }

    /**
     * 删除文章
     *
     * @param cid
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo delete(@RequestParam int cid, HttpServletRequest request) {
        try {
            contentsService.deleteByCid(cid);
            logService.insertLog(LogActions.DEL_ARTICLE.getAction(), cid + "", request.getRemoteAddr(), this.getUid(request));
        } catch (Exception e) {
            String msg = "文章删除失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }

    /**
     * @return : java.lang.String
     * @author Jesse-liu
     * @date 2020/4/21
     * @description: 页面跳转
     **/
    @RequestMapping("toImportArticles")
    public String toImportArticles() {
        return "admin/import_articles";
    }

    /**
     * @return : java.lang.String
     * @author Jesse-liu
     * @date 2020/4/21
     * @description: 页面跳转
     **/
    @PostMapping("importArticles")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo importArticles(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        try {
            // 判断文件是否存在
            if (null == file) {
                // throw new FileNotFoundException("文件不存在！");
                return RestResponseBo.fail("文件不存在");
            }
            // 判断文件大小
            if (!FileUtils.checkFileSize(file.getSize(), 1, "M")) {
                // throw new IOException(fileName + "不是excel文件");
                return RestResponseBo.fail("文件大于1M");
            }
            // 获得文件名
            String fileName = file.getOriginalFilename();
            // 判断文件是否是excel文件
            if (!fileName.endsWith("xls") && !fileName.endsWith("xlsx")) {
                // throw new IOException(fileName + "不是excel文件");
                return RestResponseBo.fail(fileName + "不是excel文件");
            }
            LOGGER.debug("Start Import Articles method");
            Boolean flag = contentsService.importArticles(request, file);
            if (!flag) {
                return RestResponseBo.fail("导入文章失败");
            }
            logService.insertLog(LogActions.IMPORT_ARTICLES.getAction(), null, request.getRemoteAddr(), TaleUtils.getLoginUser(request).getUid());

        } catch (Exception e) {
            return RestResponseBo.fail("导入文章失败");
        }
        return RestResponseBo.ok();
    }

    /**
     * @param request
     * @param response
     * @return : java.lang.String
     * @author Jesse-liu
     * @date 2020/4/22
     * @description: 导入模板下载
     **/
    @GetMapping("importTemplate/download")
    public void download(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("文章导入模板", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), ContentVo.class).sheet("文章导入模板").doWrite(null);
    }

}
