package Dao;

import java.util.List;
import model.EfpXfzdab;
import org.springframework.stereotype.Repository;

@Repository
public interface EfpXfzdabMapper {
    int deleteByPrimaryKey(String id);

    List<EfpXfzdab> selectAll();

    List<EfpXfzdab> selectSelective(EfpXfzdab record);

    int insertSelective(EfpXfzdab record);

    int updateByPrimaryKeySelective(EfpXfzdab record);
}