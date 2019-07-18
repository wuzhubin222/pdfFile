package Dao;


import model.CertApplyData;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface CertApplyDataMapper {
    int deleteByPrimaryKey(String id);

    List<CertApplyData> selectAll();

    List<CertApplyData> selectSelective(CertApplyData record);

    int insertSelective(CertApplyData record);

    int updateByPrimaryKeySelective(CertApplyData record);

    int selectSelectivesCount(CertApplyData certApplyData);

    List<HashMap<String,Object>> selectSelectives(CertApplyData certApplyData);
}