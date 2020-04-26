package com.my.blog.website.service.impl;

import com.my.blog.website.dao.MailBoxVoMapper;
import com.my.blog.website.modal.Vo.MailBoxVo;
import com.my.blog.website.service.IMaliBoxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Jesse-liu
 * @date 2020/4/21
 * @description: 邮件发送实现类
 **/
@Service
public class MailBoxServiceImpl implements IMaliBoxService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailBoxServiceImpl.class);

    @Resource
    private MailBoxVoMapper mailBoxVoMapper;

    @Override
    public MailBoxVo select() {
        return mailBoxVoMapper.select();
    }


    @Override
    public void save(MailBoxVo mailBoxVo) {
        mailBoxVoMapper.insert(mailBoxVo);
    }

    @Override
    public MailBoxVo selectById(Integer id) {
        if (null != id) {
            return mailBoxVoMapper.selectByPrimaryKey(id);
        }
        return null;
    }

    public int updateByPrimaryKey(MailBoxVo mailBoxVo) {
        if (null != mailBoxVo) {
            return mailBoxVoMapper.updateByPrimaryKey(mailBoxVo);
        }
        return -1;
    }

}
