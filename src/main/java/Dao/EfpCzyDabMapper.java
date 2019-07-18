package Dao;

import java.util.List;
import model.EfpCzyDab;
import org.springframework.stereotype.Repository;

@Repository
public interface EfpCzyDabMapper {
    int deleteByPrimaryKey(String id);

    List<EfpCzyDab> selectAll();

    List<EfpCzyDab> selectSelective(EfpCzyDab record);

    int insertSelective(EfpCzyDab record);

    int updateByPrimaryKeySelective(EfpCzyDab record);

}