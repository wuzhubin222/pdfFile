package Dao;

import java.util.HashMap;
import java.util.List;
import model.EfpFileTree;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EfpFileTreeMapper {

    List<HashMap<String,Object>> QueryByFileName(@Param("fileName") String fileName);

    int deleteByPrimaryKey(String id);

    List<EfpFileTree> selectAll();

    List<EfpFileTree> selectSelective(EfpFileTree record);

    int insertSelective(EfpFileTree record);

    int updateByPrimaryKeySelective(EfpFileTree record);
}