var annualMonthlyTaxableSalary = ['TAX', 'myfunction', 'annualMonthlyTaxableSalary', '年度月薪计税工资'];
var annualAllowance = ['myfunction', 'annualAllowance', '年度免税额'];
var annualChildrenEducation = ['myfunction', 'annualChildrenEducation', '年度子女教育'];
var annualContinuingEducation = ['myfunction', 'annualContinuingEducation', '年度继续教育'];
var annualSeriousIllnessTreatment = ['myfunction', 'annualSeriousIllnessTreatment', '年度大病医疗'];
var annualMortgageInterest = ['myfunction', 'annualMortgageInterest', '年度房贷利息'];
var annualHousingRent = ['myfunction', 'annualHousingRent', '年度住房租金'];
var annualSupportForElderly = ['SALARY', 'myfunction', 'annualSupportForElderly', '年度赡养老人'];
var annualSpecialDeductionAdjustment = ['myfunction', 'annualSpecialDeductionAdjustment', '年度专项扣减调整'];
var personalTaxPaid = ['myfunction', 'personalTaxPaid', '已纳个人所得税'];
var calculateTax = ['myfunction_1', 'calculateTax', '计算个人所得税'];

var folder_salary = [annualMonthlyTaxableSalary, annualAllowance, annualChildrenEducation, annualContinuingEducation, annualSeriousIllnessTreatment, annualMortgageInterest, annualHousingRent];
var folder_tax = [annualSupportForElderly, annualSpecialDeductionAdjustment, personalTaxPaid, calculateTax];
//内置函数数据
var N_Function = [
    {
        allData: [
            { code: 'foundYear', customFlag: 'N', groupCode: 'sdsd', name: '成立年数', defaultType: '3', orgId: 'AEA82BC80D3C441198C0DB4792C69D57', paramNum: 0 },
            { code: 'grToday', customFlag: 'N', groupCode: '', name: '今日涨幅', defaultType: '3', orgId: 'AEA82BC80D3C441198C0DB4792C69D57', paramNum: 0 },
            { code: 'grl1Week', customFlag: 'N', groupCode: '', name: '最近1周涨幅', defaultType: '3', orgId: 'AEA82BC80D3C441198C0DB4792C69D57', paramNum: 0 },
            { code: 'grl1Month', customFlag: 'N', groupCode: '', name: '最近1月涨幅', defaultType: '3', orgId: 'AEA82BC80D3C441198C0DB4792C69D57', paramNum: 0 },
            { code: 'grl3Month', customFlag: 'N', groupCode: '', name: '最近3月涨幅', defaultType: '3', orgId: 'AEA82BC80D3C441198C0DB4792C69D57', paramNum: 0 },
            { code: 'grl6Month', customFlag: 'N', groupCode: '', name: '最近6月涨幅', defaultType: '3', orgId: 'AEA82BC80D3C441198C0DB4792C69D57', paramNum: 0 },
            { code: 'grThisYear', customFlag: 'N', groupCode: '', name: '今年涨幅', defaultType: '3', orgId: 'AEA82BC80D3C441198C0DB4792C69D57', paramNum: 0 },
            { code: 'grl1Year', customFlag: 'N', groupCode: '', name: '最近1年涨幅', defaultType: '3', orgId: 'AEA82BC80D3C441198C0DB4792C69D57', paramNum: 0 },
            { code: 'grl2Year', customFlag: 'N', groupCode: '', name: '最近2年涨幅', defaultType: '3', orgId: 'AEA82BC80D3C441198C0DB4792C69D57', paramNum: 0 },
            { code: 'grl3Year', customFlag: 'N', groupCode: '', name: '最近3年涨幅', defaultType: '3', orgId: 'AEA82BC80D3C441198C0DB4792C69D57', paramNum: 0 },
            { code: 'grl5Year', customFlag: 'N', groupCode: '', name: '最近5年涨幅', defaultType: '3', orgId: 'AEA82BC80D3C441198C0DB4792C69D57', paramNum: 0 },
            { code: 'grBase', customFlag: 'N', groupCode: '', name: '成立以来涨幅', defaultType: '3', orgId: 'AEA82BC80D3C441198C0DB4792C69D57', paramNum: 0 },
            { code: 'morningRate', customFlag: 'N', groupCode: '', name: '晨星评级', defaultType: '3', orgId: 'AEA82BC80D3C441198C0DB4792C69D57', paramNum: 0 },
            { code: 'netValue', customFlag: 'N', groupCode: '', name: '净值', defaultType: '3', orgId: 'AEA82BC80D3C441198C0DB4792C69D57', paramNum: 0 },
            { code: 'totalShare', customFlag: 'N', groupCode: '', name: '规模', defaultType: '3', orgId: 'AEA82BC80D3C441198C0DB4792C69D57', paramNum: 0 },
            { code: 'type', customFlag: 'N', groupCode: '', name: '基金类型', defaultType: '3', orgId: 'AEA82BC80D3C441198C0DB4792C69D57', paramNum: 0 },
            { code: 'managers', customFlag: 'N', groupCode: '', name: '基金经理是', defaultType: '3', orgId: 'AEA82BC80D3C441198C0DB4792C69D57', paramNum: 1 },
            { code: 'cagr', customFlag: 'N', groupCode: '', name: '复合年均增长率', defaultType: '3', orgId: 'AEA82BC80D3C441198C0DB4792C69D57', paramNum: 0 }
        ],
        group: '内置函数'
    }
]
//工资科目数据
var salarySubject = [
    {
        allData: [
            { code: "C10001", name: "工资科目1", effecttiveDate: 1600100000000, paramNum: 0, customFlag: 'subject'},
            { code: "C10002", name: "工资科目2", effecttiveDate: 1600200000000, paramNum: 0, customFlag: 'subject' }
        ],
        group: '工资科目'
    }
]

function getFuncData() {
    var data = N_Function.concat(salarySubject);
    // var funcData = folder_salary.concat(folder_tax);
    return data;
};
function getVariablesData() {
    var variablesData = [['工资标准', 'salaryStandard'], ['执行系数', 'factor'], ['本月工资', 'monthSalary'], ['计税基数', 'taxBase'], ['应纳个人所得税', 'personalTaxPayable'], ['工资个人所得税', 'personalTaxOnWages']];
    return variablesData;
};
