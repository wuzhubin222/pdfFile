package Dao;

import java.util.List;
import model.EfpNsrDab;
import org.springframework.stereotype.Repository;

@Repository
public interface EfpNsrDabMapper {

    int deleteByPrimaryKey(String id);

    List<EfpNsrDab> selectAll();

    List<EfpNsrDab> selectSelective(EfpNsrDab efpNsrDab);

   int updateByPrimary(String nsrsbh);

}