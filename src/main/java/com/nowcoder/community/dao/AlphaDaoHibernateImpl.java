package com.nowcoder.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository("alphaHibernate")//访问数据库的bin
public class AlphaDaoHibernateImpl implements AlphaDao{
    @Override
    public String selsct() {
        return "Hibernate";
    }
}
