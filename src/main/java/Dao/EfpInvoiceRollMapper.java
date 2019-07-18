package Dao;

import java.util.List;
import model.EfpInvoiceRoll;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EfpInvoiceRollMapper {
    int deleteByPrimaryKey(String id);

    List<EfpInvoiceRoll> selectAll();

    List<EfpInvoiceRoll> selectSelective(EfpInvoiceRoll record);

    int insertSelective(EfpInvoiceRoll record);

    int updateByPrimaryKeySelective(EfpInvoiceRoll record);

    List<EfpInvoiceRoll> select1(@Param("nsrsbh") String nsrsbh);
}