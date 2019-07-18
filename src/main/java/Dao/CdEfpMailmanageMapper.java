package Dao;

import model.CdEfpMailmanage;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface CdEfpMailmanageMapper {
    int deleteByPrimaryKey(String id);
    int insert(CdEfpMailmanage cdEfpMailmanage);

    List<HashMap<String, String>> selectUrl();

    int updateUrlsState(@Param("items") List<String> item);
}