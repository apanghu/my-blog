package com.my.blog.website.service;

import com.my.blog.website.modal.Vo.MailBoxVo;

/**
 * Created by wangq on 2017/3/20.
 */
public interface IMaliBoxService {


    void save(MailBoxVo mailBoxVo);

    MailBoxVo selectById(Integer id);

    int updateByPrimaryKey(MailBoxVo mailBoxVo);

    MailBoxVo select();
}
