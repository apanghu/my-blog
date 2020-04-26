package com.my.blog.website.dao;

import com.my.blog.website.modal.Vo.MailBoxVo;
import org.springframework.stereotype.Component;

/**
 * @author Jesse-liu
 * @description: 邮件发送Dao层
 * @date 2020/4/21 14:45
 */
@Component
public interface MailBoxVoMapper {
    int insert(MailBoxVo mailBoxVo);

    MailBoxVo select();

    int updateByPrimaryKey(MailBoxVo mailBoxVo);

    MailBoxVo selectByPrimaryKey(Integer id);
}
