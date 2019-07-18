package Dao;

import model.EfpZpInvoiceQd;
import model.EfpZyfpInvoiceQd;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EfpZyfpInvoiceQdMapper {
    int deleteByPrimaryKey(String id);

    List<EfpZyfpInvoiceQd> selectAll();

    List<EfpZyfpInvoiceQd> selectSelective(EfpZyfpInvoiceQd record);

    int insertSelective(EfpZyfpInvoiceQd record);

    int updateByPrimaryKeySelective(EfpZyfpInvoiceQd record);
}