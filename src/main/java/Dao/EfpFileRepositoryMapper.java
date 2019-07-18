package Dao;

import model.EfpFileRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface EfpFileRepositoryMapper {
    int deleteByPrimaryKey(String id);

    List<EfpFileRepository> selectAll();

    List<EfpFileRepository> selectSelective(EfpFileRepository record);

    int insertSelective(EfpFileRepository record);

    int updateByPrimaryKeySelective(EfpFileRepository record);
}