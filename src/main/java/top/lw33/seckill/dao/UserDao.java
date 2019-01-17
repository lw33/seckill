package top.lw33.seckill.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
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
}
