package com.ido85.party.aaaa.mgmt.dto.assist;

import com.ido85.party.aaaa.mgmt.security.utils.excel.ExcelField;
import lombok.Data;

@Data
public class ImportAssistorDto {
    @ExcelField(title = "身份证号", align = 1, sort = 20)
    private String idCard;
    @ExcelField(title = "姓名", align = 1, sort = 20)
    private String relName;
    @ExcelField(title = "手机号", align = 1, sort = 20)
    private Long telephone;
    @ExcelField(title = "单位和职务", align = 1, sort = 20)
    private String remark;

    private String isExist;
}
