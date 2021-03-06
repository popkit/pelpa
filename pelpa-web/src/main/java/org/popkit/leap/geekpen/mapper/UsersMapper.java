package org.popkit.leap.geekpen.mapper;

import org.apache.ibatis.annotations.Param;
import org.popkit.leap.geekpen.entity.Users;

public interface UsersMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Users
     *
     * @mbggenerated Sat Mar 03 21:47:15 CST 2018
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Users
     *
     * @mbggenerated Sat Mar 03 21:47:15 CST 2018
     */
    int insert(Users record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Users
     *
     * @mbggenerated Sat Mar 03 21:47:15 CST 2018
     */
    int insertSelective(Users record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Users
     *
     * @mbggenerated Sat Mar 03 21:47:15 CST 2018
     */
    Users selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Users
     *
     * @mbggenerated Sat Mar 03 21:47:15 CST 2018
     */
    int updateByPrimaryKeySelective(Users record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Users
     *
     * @mbggenerated Sat Mar 03 21:47:15 CST 2018
     */
    int updateByPrimaryKey(Users record);

    Users selectByOpenid(@Param(value = "openid") String openid);
}