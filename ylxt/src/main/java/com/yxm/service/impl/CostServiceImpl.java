package com.yxm.service.impl;

import com.yxm.dao.CostDao;
import com.yxm.dao.NursetypeDao;
import com.yxm.po.SysCollection;
import com.yxm.po.SysElder;
import com.yxm.po.SysIncome;
import com.yxm.po.SysNursetype;
import com.yxm.service.CostService;
import com.yxm.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Service
public class CostServiceImpl implements CostService {

    @Autowired
    private CostDao costDao;

    @Autowired
    private NursetypeDao nursetypeDao;
    @Override
    public List<SysElder> selectElder() {
        return costDao.selectElder();
    }

    @Override
    public ElderIndentData selectElderData(Integer elderId) {
        return this.costDao.selectElderData(elderId);
    }

    @Override
    public NursetTypeCost selectNursetTypeCost(Integer elderId) {
        return this.nursetypeDao.selectNursetTypeCost(elderId);
    }

    @Override
    public boolean insertElderOrder(SysCollection sysCollection) {
        return this.nursetypeDao.insertElderOrder(sysCollection)>0;
    }

    @Override
    public SysNursetype selectNurseType(Integer id) {
        return this.nursetypeDao.selectNurseType(id);
    }


    @Override
    public IncomeColumnarVo selectIncomeColumnarVo(String years) {
        IncomeColumnarVo incomeColumnarVo =new IncomeColumnarVo();
        monthColumnarData monthColumnarData = new monthColumnarData();
        yAxisColumnarData yAxisColumnarData = new yAxisColumnarData();
        seriesColumnarData seriesColumnarData = new seriesColumnarData();
        List<BigDecimal> list = new ArrayList<>();
        List<seriesColumnarData> list1 = new ArrayList<>();
        SysMonthVo sysMonthVo = this.costDao.selectMonthVo(years);
        BigDecimal count = new BigDecimal(0);
        if(sysMonthVo!=null){
            list.add(sysMonthVo.getJan());
            list.add(sysMonthVo.getFeb());
            list.add(sysMonthVo.getMar());
            list.add(sysMonthVo.getApr());
            list.add(sysMonthVo.getMay());
            list.add(sysMonthVo.getJune());
            list.add(sysMonthVo.getJuly());
            list.add(sysMonthVo.getAug());
            list.add(sysMonthVo.getSept());
            list.add(sysMonthVo.getOct());
            list.add(sysMonthVo.getNov());
            list.add(sysMonthVo.getDece());
            //???????????????
            BigDecimal Jan = sysMonthVo.getJan();
            BigDecimal Feb = sysMonthVo.getFeb();
            BigDecimal Mar = sysMonthVo.getMar();
            BigDecimal Apr = sysMonthVo.getApr();
            BigDecimal May = sysMonthVo.getMay();
            BigDecimal June = sysMonthVo.getJune();
            BigDecimal July = sysMonthVo.getJuly();
            BigDecimal Aug = sysMonthVo.getAug();
            BigDecimal Sept = sysMonthVo.getSept();
            BigDecimal Oct = sysMonthVo.getOct();
            BigDecimal Nov = sysMonthVo.getNov();
            BigDecimal Dece = sysMonthVo.getDece();

            count = Jan.add(Feb).add(Mar).add(Apr).add(May).add(June).add(July).add(Aug).add(Sept).add(Oct).add(Nov).add(Dece);
        }else {
            for (int i = 0;i<12;i++){
                list.add(new BigDecimal(0));
            }
        }
        seriesColumnarData.setData(list);
        list1.add(seriesColumnarData);
        optionColumnarData optionColumnarData = new optionColumnarData();
        optionColumnarData.setxAxis(monthColumnarData);
        optionColumnarData.setyAxis(yAxisColumnarData);
        optionColumnarData.setSeries(list1);




        //???????????????
        BigDecimal sum = new BigDecimal(0);
        if(null !=list){
            sum = list.get(0);
            for (int i = 1; i < list.size(); i++) {
                sum = sum.max(list.get(i));
            }
        }
        BigDecimal avg =new BigDecimal(0);
        if(null !=list){
            //?????????
            BigDecimal average = list.get(0);
            for (int i = 1; i < list.size(); i++) {
                average = average.add(list.get(i));
            }
            BigDecimal sum1 = new BigDecimal(list.size());
            avg = average.divide(sum1, 2);
        }

        String stringDate="";
        if(count.compareTo(new BigDecimal(1000000))==-1){
            stringDate = "???"+years+"??????????????????"+count+"???,?????????????????????,?????????????????????????????????,????????????????????????,???????????????????????????"+sum+"?????????????????????"+avg+"????????????????????????";
        }
        if (count.compareTo(new BigDecimal(1000000))==1){
            stringDate = "???"+years+"??????????????????"+count+"???,?????????????????????,???????????????????????????"+sum+"?????????????????????"+avg+",???????????????";
        }

        incomeColumnarVo.setOptionColumnarData(optionColumnarData);
        incomeColumnarVo.setStringDate(stringDate);
        incomeColumnarVo.setCount(count);
        incomeColumnarVo.setYears(years);
        if(sysMonthVo!=null){
            incomeColumnarVo.setSysMonthVo(sysMonthVo);
        }else {
            incomeColumnarVo.setSysMonthVo(new SysMonthVo());
        }
        return incomeColumnarVo;
    }

    @Override
    public LayuiTableData<SysIncome> selectIncomeAll(Integer page, Integer limit) {
        Integer count = this.costDao.selectIncomeCount();
        List<SysIncome> data = this.costDao.selectIncomeAll(page,limit);
        return new LayuiTableData<>(count,data);
    }


}
