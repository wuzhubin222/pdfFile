package Dao;

import java.util.HashMap;
import java.util.List;

import model.EfpGrSpBm;
import org.springframework.stereotype.Repository;

@Repository
public interface EfpGrSpBmMapper {
    int deleteByPrimaryKey(String id);

    List<EfpGrSpBm> selectAll();

    List<EfpGrSpBm> selectSelective(EfpGrSpBm record);

    List<HashMap<String, String>> selectMcByNsrsbh(EfpGrSpBm record);

    int insertSelective(EfpGrSpBm record);

    int updateByPrimaryKeySelective(EfpGrSpBm record);
}