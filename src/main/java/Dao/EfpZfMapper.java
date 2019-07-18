package Dao;
import excption.BusinessException;
import model.EfpInvoiceCopy;
import model.EfpZf;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EfpZfMapper {
    int deleteByPrimaryKey(String id);

    int insert(EfpZf record);

    int insertSelective(EfpZf record);

    EfpZf selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(EfpZf record);

    int updateByPrimaryKey(EfpZf record);

    String selectnsrmc(String nsrsbh);
    String selectGfmcByFpdmAndFphm(String fphm,String fpdm);
    int insertInfo(EfpZf cdefpzf) throws BusinessException;

    List<EfpZf> selectSelective(EfpZf record);


}