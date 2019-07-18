package Dao;

import model.EfpZpInvoice;
import model.EfpZyfpInvoice;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EfpZyfpInvoiceMapper {
    int deleteByPrimaryKey(String id);

    List<EfpZyfpInvoice> selectAll();

    List<EfpZyfpInvoice> selectSelective(EfpZyfpInvoice record);

    int insertSelective(EfpZyfpInvoice record);

    int updateByPrimaryKeySelective(EfpZyfpInvoice record);
}