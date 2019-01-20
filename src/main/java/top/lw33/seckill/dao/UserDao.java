package top.lw33.seckill.dao;

import org.apache.ibatis.annotations.*;
import top.lw33.seckill.entity.User;

/**
 * @Author lw
 * @Date 2019-01-16 20:52:10
 **/
@Mapper
public interface UserDao {

    @Select("select * from `user` where id=#{id}")
    User selectUserById(long id);

    @Insert("insert into `user`(id, password, salt, nickname) values(#{id}, #{password}, #{salt}, #{nickname})")
    void insertUser(User user);

    @Update("update `user` set password=#{password} where id=#{id}")
    void updateUserPassword(@Param("id") long id, @Param("password") String password);
}
