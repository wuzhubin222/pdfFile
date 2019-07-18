package Dao;

import model.CertUser;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertUserMapper {
    int deleteByPrimaryKey(String id);

    List<CertUser> selectAll();

    List<CertUser> selectSelective(CertUser record);

    int insertSelective(CertUser record);

    int updateByPrimaryKeySelective(CertUser record);
}