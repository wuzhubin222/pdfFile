package Dao;

import java.util.HashMap;
import java.util.List;
import model.EfpKpTop;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EfpKpTopMapper {
    int deleteByPrimaryKey(String id);

    List<EfpKpTop> selectAll();

    List<EfpKpTop> selectSelective(EfpKpTop record);

    int insertSelective(EfpKpTop record);

    int updateByPrimaryKeySelective(EfpKpTop record);

    List<HashMap<String,String>> selectKpTops(@Param("phone") String phone, @Param("pageOff") Integer pageOff, @Param("pageSize") Integer pageSize);

}