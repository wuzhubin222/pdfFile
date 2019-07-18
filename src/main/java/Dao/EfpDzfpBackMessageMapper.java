package Dao;

import java.util.List;
import model.EfpDzfpBackMessage;
import org.springframework.stereotype.Repository;

@Repository
public interface EfpDzfpBackMessageMapper {
    int deleteByPrimaryKey(Long id);

    List<EfpDzfpBackMessage> selectAll();

    List<EfpDzfpBackMessage> selectSelective(EfpDzfpBackMessage record);

    int insertSelective(EfpDzfpBackMessage record);

    int updateByPrimaryKeySelective(EfpDzfpBackMessage record);
}