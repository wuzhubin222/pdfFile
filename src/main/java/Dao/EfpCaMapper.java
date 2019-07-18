package Dao;

import java.util.List;
import model.EfpCa;
import org.springframework.stereotype.Repository;

@Repository
public interface EfpCaMapper {
    int deleteByPrimaryKey(String id);

    List<EfpCa> selectAll();

    List<EfpCa> selectSelective(EfpCa record);

    int insertSelective(EfpCa record);

    int updateByPrimaryKeySelective(EfpCa record);
    int updateByNsrsbh(EfpCa record);
}