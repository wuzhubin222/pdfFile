package Dao;

import java.util.HashMap;
import java.util.List;
import model.EfpOtherUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EfpOtherUserMapper {
    int deleteByPrimaryKey(String id);

    List<EfpOtherUser> selectAll();

    List<EfpOtherUser> selectSelective(EfpOtherUser record);

    int insertSelective(EfpOtherUser record);

    int updateByPrimaryKeySelective(EfpOtherUser record);


}