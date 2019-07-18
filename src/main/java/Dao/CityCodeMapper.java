package Dao;

import java.util.List;
import model.CityCode;
import org.springframework.stereotype.Repository;

@Repository
public interface CityCodeMapper {
    int deleteByPrimaryKey(String code);

    List<CityCode> selectAll();

    List<CityCode> selectSelective(CityCode record);

    int insertSelective(CityCode record);

    int updateByPrimaryKeySelective(CityCode record);
}