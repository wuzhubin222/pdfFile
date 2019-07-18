package Dao;

import java.util.List;
import model.EfpCaMaterial;
import org.springframework.stereotype.Repository;

@Repository
public interface EfpCaMaterialMapper {

    int deleteByPrimaryKey(String id);

    List<EfpCaMaterial> selectAll();

    List<EfpCaMaterial> selectSelective(EfpCaMaterial record);

    int insertSelective(EfpCaMaterial record);

    int updateByPrimaryKeySelective(EfpCaMaterial record);
}