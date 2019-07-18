package Dao;

import java.util.List;
import model.EfpUser;
import org.springframework.stereotype.Repository;

@Repository
public interface EfpUserMapper {

    int deleteByPrimaryKey(String id);

    List<EfpUser> selectAll();

    List<EfpUser> selectSelective(EfpUser record);

    int insertSelective(EfpUser record);

    int updateByPrimaryKeySelective(EfpUser record);

}