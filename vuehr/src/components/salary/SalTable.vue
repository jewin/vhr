<template>
  <div>
    <el-container>
      <el-header style="text-align: left;padding-left: 0px;margin-top: 10px; margin-bottom: 20px;">
        <el-tag size="medium">请选择部门:</el-tag>
        <el-select size="mini" v-model="queryDepartmentId" placeholder="请选择">
          <el-option
            v-for="item in deps"
            :key="item.id"
            :label="item.name"
            :value="item.id">
          </el-option>
        </el-select>
        <label style="margin-left: 10px;">员工姓名：
          <el-input
          placeholder="员工姓名"
          clearable
          style="width: 100px;margin: 0px;padding: 0px;"
          size="mini"
          prefix-icon="el-icon-search"
          v-model="queryEmployeeName"
          >
          </el-input>
        </label>
        <label style="margin-left: 10px;">
        账套：
          <el-select size="mini" v-model="queryAccModeId" placeholder="请选择账套">
            <el-option
              v-for="item in salaryAccountModes"
              :key="item.id"
              :label="item.modeName"
              :value="item.id">
            </el-option>
        </el-select>
        </label>
        <label style="margin-left: 10px;">
        时间：
          <el-date-picker
            v-model="querySalaryMonth"
            unlink-panels
            size="mini"
            type="month"
            value-format="yyyy-MM"
            width="50px"
            >
          </el-date-picker>
        </label>
        <div style="float: right; margin-top: 10px;margin-bottom: 10px;height: 50px;">
          <el-button  @click="loadSalaryDetails" type="primary" size="mini" style="margin-left: 15px" icon="el-icon-search" >搜索</el-button>
          <el-button  @click="restQueryConditon" type="primary" size="mini" style="margin-left: 15px" icon="el-icon-setting" >重置</el-button>
        </div>
      </el-header>


      <el-main style="margin-left: 0px; margin-top: 0px; margin-right: 0px; padding-left: 0px;padding-top: 0px;">
        <div style="margin-top: 0px">
          <el-table
            :data="salaryDetails"
            size="mini"
            border
            stripe
            v-loading="tableLoading"
            @cell-dblclick="cellDbClick"
            style="width: 100%">
            <el-table-column
              align="center"
              fixed
              label="员工姓名">
            <template slot-scope="scope">{{scope.row.employeeName}}</template>
            </el-table-column>
            <el-table-column
              align="center"
              fixed
              label="所属部门">
            <template slot-scope="scope">
                {{scope.row.departmentName}}
            </template>
            </el-table-column>
            <el-table-column
              align="center"
              fixed
              label="岗位">
              <template slot-scope="scope">
                {{scope.row.position}}
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              fixed
              label="当月出勤天数">
              <template slot-scope="scope">
                {{scope.row.totalWorkDays}}
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              fixed
              label="实际出勤天数">
              <template slot-scope="scope">
                {{scope.row.actWorkDays}}
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              label="实发工资">
              <template slot-scope="scope">
                {{(scope.row.paySalary).toFixed(2)}}
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              label="现金工资">
              <template slot-scope="scope">
                {{(scope.row.cashSalary).toFixed(2)}}
              </template>
            </el-table-column>
            <el-table-column
              v-for="salaryItem in allSalaryItems" :key="salaryItem.id"
              align="center"
              :label="salaryItem.itemName"
              >
              <template slot-scope="scope">
                <label size="large" v-for="item in scope.row.salaryMonthDetail"
                       v-if="item.itemId == salaryItem.id"
                       :key="item.id">
                  {{(item.itemAmt).toFixed(2)}}
                </label>
              </template>
            </el-table-column>

          </el-table>
          <div style="text-align: right;margin-top: 10px">
            <el-pagination
              background
              @current-change="currentChange"
              layout="prev, pager, next"
              :page-size="pageSize"
              :total="totalCount">
            </el-pagination>
          </div>
        </div>
          <el-form ref="salaryDetailForm" style="margin: 0px;padding: 0px;">
            <div class="diaglog-div" style="text-align: left">
              <el-dialog
                title="工资明细"
                style="padding: 0px;"
                @close="closeDialog"
                :visible.sync="dialogVisible"
                :close-on-click-modal="false"
                width="80%">
                <el-row>
                  <el-col :span="8">
                    <div>
                      <label>姓名:  {{selectedTableRow.employeeName}}</label>
                    </div>
                  </el-col>
                  <el-col :span="8">
                    <div>
                      <label>所属部门：{{selectedTableRow.departmentName}}</label>
                    </div>
                  </el-col>
                  <el-col :span="8">
                    <div>
                      <label>账套：{{selectedTableRow.modeName}}</label>
                    </div>
                  </el-col>
                </el-row>
                <el-row>
                  <el-col :span="8">
                    <div>
                      <label>当月出勤天数:  {{selectedTableRow.totalWorkDays}}</label>
                    </div>
                  </el-col>
                  <el-col :span="8">
                    <div>
                      <label>实际出勤天数：{{selectedTableRow.actWorkDays}}</label>
                    </div>
                  </el-col>
                  <el-col :span="8">
                    <div>
                    </div>
                  </el-col>
                </el-row>
                <hr style="margin-top: 20px;">
                <el-row v-for="(item, index) in incomeDetails" :key="key = item.id"
                  v-if="index % 3 === 0">
                  <el-col :span="8">
                    <div>
                      <el-form-item :label="incomeDetails[index].itemName" prop="email">
                        <el-input
                                  size="mini" style="width: 150px" type="number"
                                  :value="incomeDetails[index].itemAmt"
                                  :placeholder="incomeDetails[index].itemName"
                                  v-model="incomeDetails[index].itemAmt"
                                  >
                        </el-input>
                      </el-form-item>
                    </div>
                  </el-col>
                  <el-col :span="8" v-if="incomeDetails[index + 1]">
                    <div>
                      <el-form-item :label="incomeDetails[index + 1].itemName" prop="email">
                        <el-input
                                  size="mini" style="width: 150px" type="number"
                                  :value="incomeDetails[index + 1].itemAmt"
                                  :placeholder="incomeDetails[index + 1].itemName"
                                  v-model="incomeDetails[index + 1].itemAmt">
                        </el-input>
                      </el-form-item>
                    </div>
                  </el-col>
                  <el-col :span="8" v-if="incomeDetails[index + 2] ">
                    <div>
                      <el-form-item :label="incomeDetails[index + 2].itemName" prop="email">
                        <el-input
                                  size="mini" style="width: 150px" type="number"
                                  :value="incomeDetails[index + 2].itemAmt"
                                  :placeholder="incomeDetails[index + 2].itemName"
                                  v-model="incomeDetails[index + 2].itemAmt">
                        </el-input>
                      </el-form-item>
                    </div>
                  </el-col>
                </el-row>
                收入类工资项目合计：{{selectedTableRow.incomeTotal}}
                <hr style="margin-top: 20px; ">
                <el-row v-for="(item, index) in outcomeDetails" :key="key = item.id"
                        v-if="index % 3 === 0">
                  <el-col :span="8">
                    <div>
                      <el-form-item :label="outcomeDetails[index].itemName" prop="email">
                        <el-input
                                  size="mini" style="width: 150px" type="number"
                                  :value="outcomeDetails[index].itemAmt"
                                  :placeholder="outcomeDetails[index].itemName"
                                  v-model="outcomeDetails[index].itemAmt">
                        </el-input>
                      </el-form-item>
                    </div>
                  </el-col>
                  <el-col :span="8" v-if="outcomeDetails[index + 1]">
                    <div>
                      <el-form-item :label="outcomeDetails[index + 1].itemName" prop="email">
                        <el-input
                          size="mini" style="width: 150px" type="number"
                          :value="outcomeDetails[index + 1].itemAmt"
                          :placeholder="outcomeDetails[index + 1].itemName"
                          v-model="outcomeDetails[index + 1].itemAmt">
                        </el-input>
                      </el-form-item>
                    </div>
                  </el-col>
                  <el-col :span="8" v-if="outcomeDetails[index + 2] ">
                    <div>
                      <el-form-item :label="outcomeDetails[index + 2].itemName" prop="email">
                        <el-input
                          size="mini" style="width: 150px" type="number"
                          :value="outcomeDetails[index + 2].itemAmt"
                          :placeholder="outcomeDetails[index + 2].itemName"
                          v-model="outcomeDetails[index + 2].itemAmt">
                        </el-input>
                      </el-form-item>
                    </div>
                  </el-col>
                </el-row>
                扣款类工资项目合计：{{(selectedTableRow.outcomeTotal || 0).toFixed(2)}}
                <hr style="margin-top: 20px; ">
                实发工资：{{(selectedTableRow.paySalary || 0).toFixed(2)}}
                <br/>
                现金工资：{{(selectedTableRow.cashSalary || 0).toFixed(2)}}
                <br/>
                最终实发: {{(selectedTableRow.cashSalary + selectedTableRow.paySalary).toFixed(2)}}
                <span slot="footer" class="dialog-footer">
                  <el-button size="mini" @click="cancelEidt">取 消</el-button>
                  <el-button size="mini" type="primary" @click="updateSalaryItemDetail(selectedTableRow)">确 定</el-button>
                </span>
              </el-dialog>
            </div>
          </el-form>

      </el-main>
    </el-container>
  </div>
</template>
<script>
  import ElInput from "../../../node_modules/element-ui/packages/input/src/input";
  import ElRow from "element-ui/packages/row/src/row";
  export default{
    components: {
      ElRow,
      ElInput},
    data(){
      return {
        deps: [],
        salaryItem : [],
        salaryAccountModes : [],
        allSalaryItems : {},
        salaryDetails : [],
        queryDepartmentId: '',
        tableLoading : false,
        dialogVisible : false,
        queryEmployeeName : '',
        queryAccModeId : '',
        currentPage : 1,
        totalCount : -1,
        pageSize : 20,
        sid : '',
        queryTelno : '',
        querySalaryMonth : '',
        selectedTableRow : {},
        incomeDetails : {},
        outcomeDetails : {},
        incomeTotal : 0,    //收入合计
        outcomeTotal : 0,    //支出合计
        fileUploadBtnText : '导入数据',
      }
    },
    methods: {
      currentChange(currentPage){
        this.currentPage = currentPage;
        this.loadSalaryDetails();
      },
      loadDeps(){
        var _this = this;
        this.getRequest("/salary/table/deps").then(resp=> {
          if (resp && resp.status == 200) {
            _this.deps = resp.data;
          }
        })
      },
      loadAccountModes(){
        var _this = this;
        if (!_this.salaryAccountModes){
          return;
        }
        this.getRequest("/salary/accountMode/findAllAccountMode").then(resp=> {
          if (resp && resp.status == 200) {
            _this.salaryAccountModes = resp.data;
          }
        })
      },
      loadAllSalaryItems() {
        var _this = this;
        if (!_this.allSalaryItems){
          return;
        }
        this.getRequest("/salary/item/findAllSalaryItems").then(resp=> {
          if (resp && resp.status == 200) {
            _this.allSalaryItems = resp.data;
          }
        })
      },

      loadSalaryDetails() {
        var _this = this;
        _this.tableLoading = true;

        var queryStr = "employeeName=" + this.queryEmployeeName + "&telno=" + this.queryTelno
          + "&departmentId=" + this.queryDepartmentId + "&accModeId=" + this.queryAccModeId
          + "&salaryMonth=" + this.querySalaryMonth
          + "&pageIndex=" + this.currentPage + "&pageSize=20"
        ;
        this.getRequest("/salary/detail/findSalaryDetails?" + queryStr).then(resp=> {
          if (resp && resp.status == 200) {
            var data = resp.data;
            _this.salaryDetails = data.list;
            _this.totalCount = data.count;
          }
        });

        _this.tableLoading = false;
      },

      closeDialog(){
        var _this = this;
        _this.dialogVisible = false;
      },
      cancelEidt(){
        this.loadSalaryDetails();
        this.dialogVisible = false;
      },
      updateSalaryItemDetail(salaryDetailForm) {
        var _this = this;
        this.postJsonRequest("/salary/detail/updateSalaryDetails",
          JSON.stringify(salaryDetailForm)
        ).then(resp=> {
          _this.tableLoading = false;
          if (resp && resp.status == 200) {
            var data = resp.data;
            _this.$message({type: data.status, message: data.msg});
              cancelEidt();
          }
        });

        _this.dialogVisible = false;

      },
      cellDbClick(row, column, cell, event){
        var _this = this;

        _this.selectedTableRow = row;
        var _incomeDetails = [],
          _outcomeDetails=[];

        row.salaryMonthDetail.forEach(function (value) {
          if (value.itemType == 1) {
            _incomeDetails.push(value);
            _this.incomeTotal = _this.incomeTotal + value.itemAmt;
          } else {
            _outcomeDetails.push(value);
            _this.outcomeTotal = _this.outcomeTotal + value.itemAmt;
          }

          _this.incomeDetails = _incomeDetails;
          _this.outcomeDetails = _outcomeDetails;

        });
        _this.dialogVisible = true;
      },
      restQueryConditon(){
        var _this = this;
        _this.querySalaryMonth = '';
        _this.queryDepartmentId = '';
        _this.queryEmployeeName  = '';
        _this.queryAccModeId = '';
      },
      changeVaue() {

      },

    },

    mounted:function () {
      Date.prototype.format = function(fmt) {
        var o = {
          "M+" : this.getMonth()+1,                 //月份
          "d+" : this.getDate(),                    //日
          "h+" : this.getHours(),                   //小时
          "m+" : this.getMinutes(),                 //分
          "s+" : this.getSeconds(),                 //秒
          "q+" : Math.floor((this.getMonth()+3)/3), //季度
          "S"  : this.getMilliseconds()             //毫秒
        };
        if(/(y+)/.test(fmt)) {
          fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
        }
        for(var k in o) {
          if(new RegExp("("+ k +")").test(fmt)){
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
          }
        }
        return fmt;
      }
      var nowDate = new Date();
      nowDate.setMonth(nowDate.getMonth() - 1);
      this.querySalaryMonth = nowDate.format("yyyy-MM");
      this.loadSalaryDetails();
      this.loadDeps();
      this.loadAccountModes();
      this.loadAllSalaryItems();
    }
  }

</script>

<style>

  .diaglog-div .el-form-item {
    margin-bottom: 10px;
  }
</style>
